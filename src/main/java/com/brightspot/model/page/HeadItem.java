package com.brightspot.model.page;

import com.brightspot.tool.Wrapper;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface HeadItem extends Recordable, Wrapper {

    default HeadItem.Data asHeadItemData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<HeadItem> {

        public static final String FIELD_PREFIX = "head.element.";

        private Boolean disabled;

        public Boolean isDisabled() {
            return Boolean.TRUE.equals(disabled);
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }
    }
}
