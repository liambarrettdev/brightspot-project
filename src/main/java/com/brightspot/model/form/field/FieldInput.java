package com.brightspot.model.form.field;

import java.util.Optional;

import com.brightspot.utils.Utils;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.util.ObjectUtils;

public abstract class FieldInput extends Field {

    public static final String TAB_ADVANCED = "Advanced";

    private String name;

    @ToolUi.Tab(TAB_ADVANCED)
    @ToolUi.Placeholder(dynamicText = "${content.getParameterNameFallback()}")
    private String parameterName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameterName() {
        return ObjectUtils.firstNonBlank(parameterName, getParameterNameFallback());
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    // -- Helper Methods -- //

    public String getParameterNameFallback() {
        return Optional.ofNullable(getName())
            .map(Utils::toNormalized)
            .orElse(null);
    }
}
