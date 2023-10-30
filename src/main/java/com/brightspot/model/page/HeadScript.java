package com.brightspot.model.page;

import com.brightspot.tool.Wrapper;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface HeadScript extends Recordable, Wrapper {

    default HeadScript.Data asHeadScriptData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<HeadScript> {

        private static final String FIELD_PREFIX = "script.";

        private Boolean disabled;

        public Boolean isDisabled() {
            return Boolean.TRUE.equals(disabled);
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }
    }
}
