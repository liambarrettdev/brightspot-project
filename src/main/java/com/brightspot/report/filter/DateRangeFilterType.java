package com.brightspot.report.filter;

import java.util.Map;

public class DateRangeFilterType extends AbstractFilterType {

    private Boolean required;

    private Boolean currentMonthByDefault;

    private Boolean recalcAvailable;

    public DateRangeFilterType(Boolean required, Boolean currentMonthByDefault, Boolean recalcAvailable) {
        this.required = required;
        this.currentMonthByDefault = currentMonthByDefault;
        this.recalcAvailable = recalcAvailable;
    }

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean isCurrentMonthByDefault() {
        return currentMonthByDefault;
    }

    public void setCurrentMonthByDefault(Boolean currentMonthByDefault) {
        this.currentMonthByDefault = currentMonthByDefault;
    }

    public Boolean isRecalcAvailable() {
        return recalcAvailable;
    }

    public void setRecalcAvailable(Boolean recalcAvailable) {
        this.recalcAvailable = recalcAvailable;
    }

    @Override
    public void updateJson(Map<String, Object> json) {
        //do nothing
    }
}
