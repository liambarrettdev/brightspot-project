package com.brightspot.integration.google.analytics;

import com.brightspot.view.integration.google.GoogleAnalyticsView;
import com.psddev.cms.view.ViewModel;

public class GoogleAnalyticsViewModel extends ViewModel<GoogleAnalytics> implements GoogleAnalyticsView {

    @Override
    public Object getTrackingId() {
        return model.getTrackingId();
    }

    @Override
    public Boolean getDisablePageViews() {
        return model.isDisablePageViews();
    }
}
