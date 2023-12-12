package com.brightspot.tool.report;

import java.util.Collections;
import java.util.List;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.Plugin;
import com.psddev.cms.tool.Tool;

@ToolUi.Hidden
public class ReportTool extends Tool {

    public static final String APPLICATION = "cms";
    public static final String TOOL_URL = "/admin/reports";
    public static final String PERMISSIONS = "area/admin/reports";

    private static final String INTERNAL_NAME = "reports";
    private static final String MENU_PARENT = "admin/adminReports";

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
}
