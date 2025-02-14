package com.brightspot.model.form.field.choice;

import java.util.Optional;

import com.brightspot.utils.Utils;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.Embedded
public class Choice extends Record {

    private String text;

    @ToolUi.Placeholder(dynamicText = "${content.getValueFallback()}")
    private String value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return ObjectUtils.firstNonBlank(value, getValueFallback());
    }

    public void setValue(String value) {
        this.value = value;
    }

    // -- Helper Methods -- //

    public String getValueFallback() {
        return Optional.ofNullable(getText())
            .map(Utils::toNormalized)
            .orElse(null);
    }
}
