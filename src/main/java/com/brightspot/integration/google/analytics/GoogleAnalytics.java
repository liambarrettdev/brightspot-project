package com.brightspot.integration.google.analytics;

import com.brightspot.model.page.HeadItem;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;

@ViewBinding(value = GoogleAnalyticsViewModel.class, types = GoogleAnalytics.VIEW_TYPE)
public class GoogleAnalytics extends Record implements HeadItem {

    protected static final String VIEW_TYPE = "google-analytics";

    @Required
    private String trackingId;

    private Boolean disablePageViews;

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public Boolean isDisablePageViews() {
        return Boolean.TRUE.equals(disablePageViews);
    }

    public void setDisablePageViews(Boolean disablePageViews) {
        this.disablePageViews = disablePageViews;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public String getLabel() {
        return getTrackingId();
    }
}
