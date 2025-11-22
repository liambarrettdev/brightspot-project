package com.brightspot.tool;

import com.psddev.cms.db.Site;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface DefaultGlobal extends Recordable {

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<DefaultGlobal> {

        public static final String FIELD_PREFIX = "default.global.";

        @Override
        public void beforeCommit() {
            if (getState().isNew()) {
                as(Site.ObjectModification.class).setGlobal(true);
                as(Site.ObjectModification.class).setOwner(null);
            }

            super.beforeCommit();
        }
    }
}
