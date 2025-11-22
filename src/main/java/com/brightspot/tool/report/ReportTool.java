package com.brightspot.tool.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.brightspot.report.AbstractReport;
import com.psddev.cms.tool.Plugin;
import com.psddev.cms.tool.Tool;
import com.psddev.dari.db.Application;
import com.psddev.dari.util.ObjectUtils;

public class ReportTool extends Tool {

    public static final String APPLICATION = "cms";
    public static final String TOOL_URL = "/admin/reports";
    public static final String PERMISSIONS = "area/admin/reports";

    private static final String INTERNAL_NAME = "reports";
    private static final String MENU_PARENT = "admin/adminReports";

    private List<AbstractReport> reports;

    public List<AbstractReport> getReports() {
        if (reports == null) {
            reports = new ArrayList<>();
        }
        return reports;
    }

    public void setReports(List<AbstractReport> reports) {
        this.reports = reports;
    }

    // -- Overrides -- //

    @Override
    public String getApplicationName() {
        return APPLICATION;
    }

    @Override
    public List<Plugin> getPlugins() {
        return Collections.singletonList(createArea2(
            "Reports", //TODO localize
            INTERNAL_NAME,
            MENU_PARENT,
            TOOL_URL)
        );
    }

    // -- Statics -- //

    public static ReportTool getInstance() {
        return Application.Static.getInstance(ReportTool.class);
    }

    public static <T> T get(Function<ReportTool, T> getter) {
        return get(getter, null);
    }

    public static <T> T get(Function<ReportTool, T> getter, T defaultValue) {
        ReportTool settings = getInstance();
        if (settings != null) {
            T value = getter.apply(settings);
            if (!ObjectUtils.isBlank(value)) {
                return value;
            }
        }
        return defaultValue;
    }
}
