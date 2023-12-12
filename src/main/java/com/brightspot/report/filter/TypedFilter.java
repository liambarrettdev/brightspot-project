package com.brightspot.report.filter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.psddev.dari.db.Query;

public class TypedFilter extends ReportFilter {

    private AbstractFilterType type;

    public AbstractFilterType getType() {
        return type;
    }

    public void setType(AbstractFilterType type) {
        this.type = type;
    }

    @Override
    public Map<String, Object> toJson(Query<?> query) {
        Map<String, Object> json = new LinkedHashMap<>();

        json.put("paramName", getName());
        json.put("label", getLabel());

        Optional.ofNullable(getType()).ifPresent(type -> type.updateJson(json));

        return json;
    }
}
