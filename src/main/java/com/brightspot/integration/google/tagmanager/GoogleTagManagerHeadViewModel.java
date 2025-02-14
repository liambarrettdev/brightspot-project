package com.brightspot.integration.google.tagmanager;

import com.brightspot.view.integration.google.GoogleTagManagerHeadView;
import com.psddev.cms.view.ViewModel;

public class GoogleTagManagerHeadViewModel extends ViewModel<GoogleTagManager> implements GoogleTagManagerHeadView {

    @Override
    public Object getContainerId() {
        return model.getContainerId();
    }

}
