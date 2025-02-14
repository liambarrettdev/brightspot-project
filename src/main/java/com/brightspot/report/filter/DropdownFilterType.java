package com.brightspot.report.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropdownFilterType extends AbstractFilterType {

    private List<DropdownOption> options;

    public DropdownFilterType(List<DropdownOption> options) {
        this.options = options;
    }

    public List<DropdownOption> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
        }
        return options;
    }

    public void setOptions(List<DropdownOption> options) {
        this.options = options;
    }

    @Override
    public void updateJson(Map<String, Object> json) {
        List<Map<String, Object>> jsonArray = new ArrayList<>();

        for (DropdownOption option : getOptions()) {
            Map<String, Object> jsonOption = new HashMap<>();

            jsonOption.put("name", option.getLabel());
            jsonOption.put("value", option.getValue());

            jsonArray.add(jsonOption);
        }

        json.put("data", jsonArray);
    }
}
