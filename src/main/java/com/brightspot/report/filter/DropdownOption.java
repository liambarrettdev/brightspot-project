package com.brightspot.report.filter;

public class DropdownOption {

    private String label;

    private String value;

    private Boolean selected;

    public DropdownOption(String label, String value) {
        this.label = label;
        this.value = value;
        this.selected = false;
    }

    public DropdownOption(String label, String value, Boolean selected) {
        this.label = label;
        this.value = value;
        this.selected = selected;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isSelected() {
        return Boolean.TRUE.equals(selected);
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
