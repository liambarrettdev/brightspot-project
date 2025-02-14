package com.brightspot.integration.google.tagmanager;

import com.brightspot.view.integration.google.GoogleTagManagerBodyView;
import com.psddev.cms.view.ViewModel;

public class GoogleTagManagerBodyViewModel extends ViewModel<GoogleTagManager> implements GoogleTagManagerBodyView {

    @Override
    public Object getContainerId() {
        return model.getContainerId();
    }
}
