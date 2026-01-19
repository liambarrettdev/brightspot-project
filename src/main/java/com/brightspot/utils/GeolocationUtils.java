package com.brightspot.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.psddev.dari.db.Location;
import com.psddev.dari.db.Region;
import com.psddev.dari.util.ObjectUtils;

/**
 * Utility class for geolocation calculations.
 *
 * <p>This class provides methods for working with geographic coordinates, including
 * distance calculations, coordinate conversions, and bounding region generation.</p>
 *
 * <p>Distance calculations use the great circle distance formula (Haversine formula)
 * which assumes a spherical Earth model.</p>
 */
public final class GeolocationUtils {

    public static final String PARAM_LAT = "lat";
    public static final String PARAM_LNG = "long";

    private static final double MIN_LAT = Math.toRadians(-90D);
    private static final double MAX_LAT = Math.toRadians(90D);
    private static final double MIN_LNG = Math.toRadians(-180D);
    private static final double MAX_LNG = Math.toRadians(180D);

    private GeolocationUtils() {
    }

    /**
     * Converts degrees to radians.
     *
     * @param deg the angle in degrees
     * @return the angle in radians
     */
    public static double degreesToRadians(double deg) {
        return (deg * Math.PI / 180.0D);
    }

    /**
     * Converts radians to degrees.
     *
     * @param rad the angle in radians
     * @return the angle in degrees
     */
    public static double radiansToDegrees(double rad) {
        return (rad * 180.0D / Math.PI);
    }

    /**
     * Converts miles to degrees of latitude/longitude.
     *
     * <p>This is a convenience method that delegates to {@link Region#milesToDegrees(double)}.</p>
     *
     * @param distance the distance in miles
     * @return the equivalent distance in degrees
     */
    public static double milesToDegrees(double distance) {
        return Region.milesToDegrees(distance);
    }

    /**
     * Creates a bounding polygon region around a location with a specified radius.
     *
     * <p>Generates a rectangular bounding box (polygon) that encompasses all points
     * within the specified radius of the location. The radius is in degrees.</p>
     *
     * @param location the center location
     * @param radius the radius in degrees
     * @return a polygon representing the bounding region
     */
    public static Region.Polygon boundingRegion(Location location, double radius) {
        double lat = location.getX();
        double lng = location.getY();
        return boundingRegion(lat, lng, radius);
    }

    /**
     * Creates a bounding polygon region around coordinates with a specified radius.
     *
     * <p>Generates a rectangular bounding box (polygon) that encompasses all points
     * within the specified radius. Handles edge cases near the poles and the
     * international date line.</p>
     *
     * <p>TODO: Consider adding validation for latitude/longitude ranges</p>
     *
     * @param lat the latitude in degrees
     * @param lng the longitude in degrees
     * @param radius the radius in degrees
     * @return a polygon representing the bounding region
     */
    public static Region.Polygon boundingRegion(double lat, double lng, double radius) {
        double radLat = degreesToRadians(lat);
        double radLng = degreesToRadians(lng);
        double radRadius = degreesToRadians(radius);

        double minLat = radLat - radRadius;
        double maxLat = radLat + radRadius;

        double minLng;
        double maxLng;
        if (minLat > MIN_LAT && maxLat < MAX_LAT) {
            double dLng = Math.asin(Math.sin(radRadius) / Math.cos(radLat));
            minLng = radLng - dLng;
            if (minLng < MIN_LNG) {
                minLng = minLng + 2D * Math.PI;
            }
            maxLng = radLng + dLng;
            if (maxLng > MAX_LNG) {
                maxLng = maxLng - 2D * Math.PI;
            }
        } else {
            // at least one pole in range
            minLat = Math.max(minLat, MIN_LAT);
            maxLat = Math.min(maxLat, MAX_LAT);
            minLng = MIN_LNG;
            maxLng = MAX_LNG;
        }
        Region.LinearRing ring = new Region.LinearRing();
        Region.Polygon polygon = new Region.Polygon();
        polygon.add(ring);

        ring.add(new Region.Coordinate(radiansToDegrees(maxLng), radiansToDegrees(maxLat)));
        ring.add(new Region.Coordinate(radiansToDegrees(maxLng), radiansToDegrees(minLat)));
        ring.add(new Region.Coordinate(radiansToDegrees(minLng), radiansToDegrees(minLat)));
        ring.add(new Region.Coordinate(radiansToDegrees(minLng), radiansToDegrees(maxLat)));
        ring.add(new Region.Coordinate(radiansToDegrees(maxLng), radiansToDegrees(maxLat)));

        return polygon;
    }

    /**
     * Calculates the distance in miles between two locations.
     *
     * <p>Uses the great circle distance formula to calculate the distance. Returns
     * the result rounded to 2 decimal places. Returns {@code null} if either
     * location is null or blank.</p>
     *
     * @param current the starting location
     * @param destination the destination location
     * @return the distance in miles rounded to 2 decimal places, or {@code null} if locations are invalid
     */
    public static Double milesBetweenLocations(Location current, Location destination) {
        if (!ObjectUtils.isBlank(current) && !ObjectUtils.isBlank(destination)) {
            return BigDecimal.valueOf(calculateCircleDistance(current, destination))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        }
        return null;
    }

    /**
     * Calculates the great circle distance between two locations in nautical miles.
     *
     * <p>Based on the Great Circle distance formula from Princeton's algorithms library.
     * Uses both the law of cosines and Haversine formula, but returns the Haversine result
     * which is more accurate for small distances.</p>
     *
     * <p>The formula calculates the shortest distance along the surface of a sphere
     * (assuming Earth is a perfect sphere). Each degree on a great circle equals 60 nautical miles.</p>
     *
     * <p>Reference: <a href="https://introcs.cs.princeton.edu/java/12types/GreatCircle.java.html">GreatCircle.java</a></p>
     *
     * <p>TODO: Consider using a more accurate ellipsoid model (WGS84) for better precision</p>
     *
     * @param current the starting location
     * @param destination the destination location
     * @return the distance in nautical miles
     */
    public static double calculateCircleDistance(Location current, Location destination) {
        double x1 = Math.toRadians(current.getX());
        double y1 = Math.toRadians(current.getY());
        double x2 = Math.toRadians(destination.getX());
        double y2 = Math.toRadians(destination.getY());

        /*
         * Compute using law of cosines
         */
        // great circle distance in radians
        double angle1 = Math.acos(Math.sin(x1) * Math.sin(x2)
            + Math.cos(x1) * Math.cos(x2) * Math.cos(y1 - y2));

        // convert back to degrees
        angle1 = Math.toDegrees(angle1);

        // each degree on a great circle of Earth is 60 nautical miles
        // TODO: distance1 is calculated but not used - consider removing law of cosines calculation or using it for validation/comparison with Haversine result
        @SuppressWarnings("unused")
        double distance1 = 60 * angle1;

        /*
         * Compute using Haversine formula
         */
        double a = Math.pow(Math.sin((x2 - x1) / 2), 2)
            + Math.cos(x1) * Math.cos(x2) * Math.pow(Math.sin((y2 - y1) / 2), 2);

        // great circle distance in radians
        double angle2 = 2 * Math.asin(Math.min(1, Math.sqrt(a)));

        // convert back to degrees
        angle2 = Math.toDegrees(angle2);

        // each degree on a great circle of Earth is 60 nautical miles
        return 60 * angle2;
    }
}
