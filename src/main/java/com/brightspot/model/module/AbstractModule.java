package com.brightspot.model.module;

import com.brightspot.tool.Wrapper;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
public abstract class AbstractModule extends Content implements Wrapper {

    public AbstractModule() {
        super();

        if (getState().isNew()) {
            Site currentSite = Utils.getCurrentSite();
            this.as(Site.ObjectModification.class).setOwner(currentSite);
        }
    }

    // -- Overrides -- //

    @Override
    public Object unwrap() {
        return this;
    }
}
