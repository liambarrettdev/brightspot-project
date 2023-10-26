package com.brightspot.model.form.field;

import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface Requireable extends Recordable {

    default Data asRequireableData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<Requireable> {

        public static final String FIELD_PREFIX = "requireable.";

        private Boolean required;

        public Boolean isRequired() {
            return Boolean.TRUE.equals(required);
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }
    }
}
