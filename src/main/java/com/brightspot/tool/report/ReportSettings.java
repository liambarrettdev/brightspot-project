package com.brightspot.tool.report;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.brightspot.report.AbstractReport;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.CmsTool;
import com.psddev.dari.db.Application;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.BeanProperty("task")
@Recordable.FieldInternalNamePrefix("settings.report.")
public class ReportSettings extends Modification<CmsTool> {

    public static final String TAB_NAME = "Reports";

    @ToolUi.Tab(TAB_NAME)
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

    public static ReportSettings getInstance() {
        return Application.Static.getInstance(CmsTool.class).as(ReportSettings.class);
    }

    public static <T> T get(Function<ReportSettings, T> getter) {
        return get(getter, null);
    }

    public static <T> T get(Function<ReportSettings, T> getter, T defaultValue) {
        ReportSettings settings = getInstance();
        if (settings != null) {
            T value = getter.apply(settings);
            if (!ObjectUtils.isBlank(value)) {
                return value;
            }
        }
        return defaultValue;
    }
}
