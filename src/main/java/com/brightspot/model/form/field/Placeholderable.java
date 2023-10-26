package com.brightspot.model.form.field;

import com.psddev.dari.db.Modification;

public interface Placeholderable extends Requireable {

    default Data asPlaceholderableData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<Placeholderable> {

        public static final String FIELD_PREFIX = "placeholderable.";

        private String placeholder;

        public String getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }
    }
}
