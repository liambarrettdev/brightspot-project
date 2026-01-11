package com.brightspot.utils;

import java.util.Optional;
import java.util.UUID;

import com.psddev.analytics.AnalyticsTool;
import com.psddev.analytics.BuiltInWidgetConfiguration;
import com.psddev.analytics.DynamicMethodFieldStruct;
import com.psddev.cms.db.Directory;
import com.psddev.dari.db.Application;
import com.psddev.dari.db.Metric;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for retrieving analytics data from the Brightspot analytics system.
 *
 * <p>This class provides methods to retrieve page views and clicks from the analytics
 * database using the AnalyticsTool configuration. It handles both page-specific analytics
 * (for content with permalinks) and general content analytics.</p>
 *
 * <p>All methods return {@code null} if the content type has analytics disabled or if
 * the content cannot be found.</p>
 */
public final class AnalyticsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsUtils.class);

    private AnalyticsUtils() {
    }

    /**
     * Gets the total page views for a content item within the specified date range.
     *
     * <p>This method retrieves analytics data from the Metrics stored on the content's
     * state. It automatically selects the appropriate metric field based on whether
     * the content has a permalink (uses page views field) or not (uses views field).</p>
     *
     * <p>Returns 0 if the metric exists but has no data, or {@code null} if the content
     * cannot be found or analytics is disabled for the content type.</p>
     *
     * @param id the UUID of the content item
     * @param start the start date of the range (inclusive)
     * @param end the end date of the range (inclusive)
     * @return the total page views as a Long, or {@code null} if content not found or analytics disabled
     */
    public static Long getPageViews(UUID id, DateTime start, DateTime end) {
        State state = findPageState(id);
        if (state == null) {
            return null;
        }

        DynamicMethodFieldStruct viewsField;
        if (state.as(Directory.ObjectModification.class).getPermalink() != null) {
            viewsField = Application.Static.getInstance(AnalyticsTool.class)
                .as(BuiltInWidgetConfiguration.class)
                .getPageViewsFieldStruct();
        } else {
            viewsField = Application.Static.getInstance(AnalyticsTool.class)
                .as(BuiltInWidgetConfiguration.class)
                .getViewsFieldStruct();
        }

        return Optional.ofNullable(state.get(viewsField.field.getInternalName()))
            .map(Metric.class::cast)
            .map(m -> m.getSumBetween(start, end))
            .map(Double::longValue)
            .orElse(0L);
    }

    /**
     * Gets the total page clicks for a content item within the specified date range.
     *
     * <p>This method retrieves click analytics data from the Metrics stored on the content's
     * state. It automatically selects the appropriate metric field based on whether
     * the content has a permalink (uses page clicks field) or not (uses clicks field).</p>
     *
     * <p>Returns 0 if the metric exists but has no data, or {@code null} if the content
     * cannot be found or analytics is disabled for the content type.</p>
     *
     * @param pageId the UUID of the content item
     * @param start the start date of the range (inclusive)
     * @param end the end date of the range (inclusive)
     * @return the total page clicks as a Long, or {@code null} if content not found or analytics disabled
     */
    public static Long getPageClicks(UUID pageId, DateTime start, DateTime end) {
        State state = findPageState(pageId);
        if (state == null) {
            return null;
        }

        DynamicMethodFieldStruct viewsField;
        if (state.as(Directory.ObjectModification.class).getPermalink() != null) {
            viewsField = Application.Static.getInstance(AnalyticsTool.class)
                .as(BuiltInWidgetConfiguration.class)
                .getPageClicksFieldStruct();
        } else {
            viewsField = Application.Static.getInstance(AnalyticsTool.class)
                .as(BuiltInWidgetConfiguration.class)
                .getClicksFieldStruct();
        }

        return Optional.ofNullable(state.get(viewsField.field.getInternalName()))
            .map(Metric.class::cast)
            .map(m -> m.getSumBetween(start, end))
            .map(Double::longValue)
            .orElse(0L);
    }

    /**
     * Finds the State object for a content item by UUID.
     *
     * <p>This method performs validation to ensure the content exists and that analytics
     * is enabled for the content type. Returns {@code null} if the content cannot be found
     * or if analytics is disabled for the content type.</p>
     *
     * @param id the UUID of the content item
     * @return the State object, or {@code null} if not found or analytics disabled
     */
    private static State findPageState(UUID id) {
        State state = State.getInstance(Query.from(Object.class).where("_id = ?", id).first());
        if (state == null) {
            LOGGER.warn("Could not find page with UUID {}", id);
            return null;
        }
        if (Application.Static.getInstance(AnalyticsTool.class)
            .as(BuiltInWidgetConfiguration.class)
            .getDisabledContentTypes()
            .contains(state.getType())) {
            LOGGER.warn("Analytics disabled for content type {}", state.getType().getDisplayName());
            return null;
        }
        return state;
    }
}
