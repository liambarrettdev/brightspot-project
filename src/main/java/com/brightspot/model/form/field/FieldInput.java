package com.brightspot.model.form.field;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.brightspot.utils.Utils;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.util.ObjectUtils;

public abstract class FieldInput extends Field implements SubmittableField {

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

    // -- Overrides -- //

    @Override
    public Map<String, String> getSubmittedValue(HttpServletRequest request) {
        return Optional.ofNullable(request)
            .map(r -> r.getParameterValues(getParameterName()))
            .map(v -> String.join(",", v))
            .map(v -> Collections.singletonMap(getParameterName(), v))
            .orElse(null);
    }

    // -- Helper Methods -- //

    public String getParameterNameFallback() {
        return Optional.ofNullable(getName())
            .map(Utils::toNormalized)
            .orElse(null);
    }
}
