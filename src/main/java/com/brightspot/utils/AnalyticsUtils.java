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

public class AnalyticsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsUtils.class);

    public static Integer getPageViews(UUID id, DateTime start, DateTime end) {
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
            .map(Double::intValue)
            .orElse(0);
    }

    public static Integer getPageClicks(UUID pageId, DateTime start, DateTime end) {
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
            .map(Double::intValue)
            .orElse(0);
    }

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
