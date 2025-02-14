package com.brightspot.model.form.field.text;

import com.brightspot.model.form.field.FieldType;
import com.brightspot.model.form.field.HasPlaceholder;
import com.psddev.cms.db.ToolUi;

@ToolUi.FieldDisplayOrder({
    "placeholderable.placeholder",
    "requireable.required"
})
public abstract class TextFieldType extends FieldType {

    public String getRegexPattern() {
        return null;
    }

    // -- Static Classes -- //

    public static class Default extends TextFieldType implements HasPlaceholder {

        public static final String FIELD_TYPE = "text";

        // -- Overrides -- //

        @Override
        public String getFieldType() {
            return FIELD_TYPE;
        }

        @Override
        public String getPlaceholder() {
            return asPlaceholderData().getPlaceholder();
        }

        @Override
        public Boolean isRequired() {
            return asRequireableData().isRequired();
        }
    }

    public static class Email extends TextFieldType implements HasPlaceholder {

        public static final String FIELD_TYPE = "email";

        private String regexPattern;

        public String getRegexPattern() {
            return regexPattern;
        }

        public void setRegexPattern(String regexPattern) {
            this.regexPattern = regexPattern;
        }

        // -- Overrides -- //

        @Override
        public String getFieldType() {
            return FIELD_TYPE;
        }

        @Override
        public String getPlaceholder() {
            return asPlaceholderData().getPlaceholder();
        }

        @Override
        public Boolean isRequired() {
            return asRequireableData().isRequired();
        }
    }

    public static class Password extends TextFieldType implements HasPlaceholder {

        public static final String FIELD_TYPE = "password";

        private String regexPattern;

        public String getRegexPattern() {
            return regexPattern;
        }

        public void setRegexPattern(String regexPattern) {
            this.regexPattern = regexPattern;
        }

        // -- Overrides -- //

        @Override
        public String getFieldType() {
            return FIELD_TYPE;
        }

        @Override
        public String getPlaceholder() {
            return asPlaceholderData().getPlaceholder();
        }

        @Override
        public Boolean isRequired() {
            return asRequireableData().isRequired();
        }
    }

    public static class Phone extends TextFieldType implements HasPlaceholder {

        public static final String FIELD_TYPE = "tel";

        private String regexPattern;

        public String getRegexPattern() {
            return regexPattern;
        }

        public void setRegexPattern(String regexPattern) {
            this.regexPattern = regexPattern;
        }

        // -- Overrides -- //

        @Override
        public String getFieldType() {
            return FIELD_TYPE;
        }

        @Override
        public String getPlaceholder() {
            return asPlaceholderData().getPlaceholder();
        }

        @Override
        public Boolean isRequired() {
            return asRequireableData().isRequired();
        }
    }
}
