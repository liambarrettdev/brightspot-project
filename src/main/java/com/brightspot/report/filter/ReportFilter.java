package com.brightspot.report.filter;

import java.util.Map;

import com.psddev.dari.db.Query;

public abstract class ReportFilter {

    public abstract Map<String, Object> toJson(Query<?> query);

    private String name;

    private String label;

    private String heading;

    private String defaultValue;

    private String extraClass;

    private Boolean onNewLine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExtraClass() {
        return extraClass;
    }

    public void setExtraClass(String extraClass) {
        this.extraClass = extraClass;
    }

    public Boolean isOnNewLine() {
        return Boolean.TRUE.equals(onNewLine);
    }

    public void setOnNewLine(Boolean onNewLine) {
        this.onNewLine = onNewLine;
    }
}
