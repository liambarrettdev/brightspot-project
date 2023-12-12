package com.brightspot.report.filter;

import java.util.Map;

public class CheckboxFilterType extends AbstractFilterType {

    private String value;

    private Boolean checkedByDefault;

    public CheckboxFilterType(String value) {
        this.value = value;
        this.checkedByDefault = false;
    }

    public CheckboxFilterType(String value, Boolean checkedByDefault) {
        this.value = value;
        this.checkedByDefault = checkedByDefault;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isCheckedByDefault() {
        return Boolean.TRUE.equals(checkedByDefault);
    }

    public void setCheckedByDefault(Boolean checkedByDefault) {
        this.checkedByDefault = checkedByDefault;
    }

    @Override
    public void updateJson(Map<String, Object> json) {
        json.put("value", isCheckedByDefault());
    }
}
