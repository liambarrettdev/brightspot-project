package com.brightspot.report.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.brightspot.report.AbstractReport;
import com.brightspot.servlet.ReportServlet;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class GenericFilter extends ReportFilter {

    @Override
    public Map<String, Object> toJson(Query<?> query) {
        Map<String, Object> json = new LinkedHashMap<>();

        json.put("paramName", getName());
        json.put("label", getLabel());

        List<Map<String, Object>> jsonArray = new ArrayList<>();
        if (AbstractReport.FILTER_BY_SITE.equalsIgnoreCase(getName())) {
            // render market/site selection (special case)
            for (Site site : Site.Static.findAll()) {
                Map<String, Object> jsonOption = new HashMap<>();

                jsonOption.put("name", site.getLabel());
                jsonOption.put("value", site.getId().toString());

                jsonArray.add(jsonOption);
            }

            json.put("data", jsonArray);

            return json;
        }

        ObjectType type = ObjectType.getInstance(query.getObjectClass());
        ObjectField field = type.getField(getName());

        if (field == null) {
            return json;
        }

        // render enum options (special case)
        if (!ObjectUtils.isBlank(field.getJavaEnumClassName())) {
            for (ObjectField.Value enumValue : field.getValues()) {
                Map<String, Object> jsonOption = new HashMap<>();

                jsonOption.put("name", enumValue.getLabel());
                jsonOption.put("value", enumValue.getValue());

                jsonArray.add(jsonOption);
            }

            json.put("data", jsonArray);

            return json;
        }

        for (Object option : findOptions(query)) {
            Map<String, Object> jsonOption = new HashMap<>();

            if (option instanceof Record) {
                jsonOption.put("name", ((Record) option).getLabel());
                jsonOption.put("value", ((Record) option).getId().toString());
            } else {
                jsonOption.put("name", option.toString());
                jsonOption.put("value", option.toString());
            }

            jsonArray.add(jsonOption);
        }

        json.put("data", jsonArray);

        return json;
    }

    private List<Object> findOptions(Query<?> query) {
        List<Object> options = new ArrayList<>();

        if (!StringUtils.isBlank(getDefaultValue())) {
            options.add(getDefaultValue());
        }

        options.addAll(ReportServlet.getAllValuesForField(query, getName()));

        ReportServlet.sort(options);

        return options;
    }
}
