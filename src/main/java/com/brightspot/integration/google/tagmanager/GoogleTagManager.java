package com.brightspot.integration.google.tagmanager;

import com.brightspot.integration.TagManager;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;

@ViewBinding(value = GoogleTagManagerHeadViewModel.class, types = TagManager.HEAD_VIEW_TYPE)
@ViewBinding(value = GoogleTagManagerBodyViewModel.class, types = TagManager.BODY_VIEW_TYPE)
public class GoogleTagManager extends Record implements TagManager {

    @Required
    private String containerId;

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return TagManager.HEAD_VIEW_TYPE;
    }
}
