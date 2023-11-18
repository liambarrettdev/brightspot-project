package com.brightspot.model.module;

import com.brightspot.tool.ViewWrapper;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
public abstract class AbstractModule extends Content implements ViewWrapper {

    public AbstractModule() {
        super();

        if (getState().isNew()) {
            Site currentSite = Utils.getCurrentSite();
            this.as(Site.ObjectModification.class).setOwner(currentSite);
        }
    }
}
