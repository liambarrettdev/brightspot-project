package com.brightspot.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.psddev.dari.db.Location;
import com.psddev.dari.db.Region;
import com.psddev.dari.util.ObjectUtils;

public final class GeolocationUtils {

    public static final String PARAM_LAT = "lat";
    public static final String PARAM_LNG = "long";

    private static final double MIN_LAT = Math.toRadians(-90D);
    private static final double MAX_LAT = Math.toRadians(90D);
    private static final double MIN_LNG = Math.toRadians(-180D);
    private static final double MAX_LNG = Math.toRadians(180D);

    private GeolocationUtils() {
    }

    public static double degreesToRadians(double deg) {
        return (deg * Math.PI / 180.0D);
    }

    public static double radiansToDegrees(double rad) {
        return (rad * 180.0D / Math.PI);
    }

    public static double milesToDegrees(double distance) {
        return Region.milesToDegrees(distance);
    }

    public static Region.Polygon boundingRegion(Location location, double radius) {
        double lat = location.getX();
        double lng = location.getY();
        return boundingRegion(lat, lng, radius);
    }

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

    public static Double milesBetweenLocations(Location current, Location destination) {
        if (!ObjectUtils.isBlank(current) && !ObjectUtils.isBlank(destination)) {
            return BigDecimal.valueOf(calculateCircleDistance(current, destination))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        }
        return null;
    }

    /******************************************************************************
     * Based on <a href="https://introcs.cs.princeton.edu/java/12types/GreatCircle.java.html">GreatCircle.java</a>
     * Compilation:  javac GreatCircle.java
     * Execution:    java GreatCircle L1 G1 L2 G2
     * <p/>
     * Given the latitude and longitude (in degrees) of two points compute
     * the great circle distance (in nautical miles) between them. The
     * following formula assumes that sin, cos, and arcos are computed
     * in degrees, so need to convert back and forth between radians.
     * <p/>
     * <code>d  = 60 * acos (sin(L1)*sin(L2) + cos(L1)*cos(L2)*cos(G1 - G2))</code>
     * <p/>
     * <pre>
     * % java GreatCircle 59.9 -30.3 37.8 122.4        // Leningrad to SF
     * 4784.369673474519 nautical miles
     * <p/>
     * % java GreatCircle 48.87 -2.33 30.27 97.74      // Paris to Austin
     * 4423.14075970742 nautical miles
     * <p/>
     * % java GreatCircle 36.12 -86.67 33.94 -118.4    // Nashville airport (BNA) to LAX
     * 1557.50511103695 nautical miles
     * <p/>
     * % java GreatCircle 40.35 74.65 48.87 -2.33      // Princeton to Paris
     * 3185.1779271158425 nautical miles
     * </pre>
     ******************************************************************************/

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
