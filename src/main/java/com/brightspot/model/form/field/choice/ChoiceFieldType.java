package com.brightspot.model.form.field.choice;

import com.brightspot.model.form.field.FieldType;
import com.brightspot.model.form.field.Placeholderable;
import com.psddev.cms.db.ToolUi;

@ToolUi.FieldDisplayOrder({
    "placeholderable.placeholder",
    "requireable.required"
})
public abstract class ChoiceFieldType extends FieldType {

    public Boolean isAllowMultiSelect() {
        return false;
    }

    // -- Static Classes -- //

    public static class Checkboxes extends ChoiceFieldType {

        public static final String FIELD_TYPE = "checkbox";

        // -- Overrides -- //

        @Override
        public String getFieldType() {
            return FIELD_TYPE;
        }
    }

    public static class Dropdown extends ChoiceFieldType implements Placeholderable {

        private Boolean allowMultiSelect;

        public Boolean isAllowMultiSelect() {
            return Boolean.TRUE.equals(allowMultiSelect);
        }

        public void setAllowMultiSelect(Boolean allowMultiSelect) {
            this.allowMultiSelect = allowMultiSelect;
        }

        // -- Overrides -- //

        @Override
        public String getFieldType() {
            return null;
        }

        @Override
        public Boolean isRequired() {
            return asRequireableData().isRequired();
        }

        @Override
        public String getPlaceholder() {
            return asPlaceholderableData().getPlaceholder();
        }
    }

    public static class RadioButtons extends ChoiceFieldType {

        public static final String FIELD_TYPE = "radio";

        // -- Overrides -- //

        @Override
        public String getFieldType() {
            return FIELD_TYPE;
        }
    }
}
