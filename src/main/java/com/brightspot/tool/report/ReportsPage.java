package com.brightspot.tool.report;

import java.io.IOException;
import javax.servlet.ServletException;

import com.brightspot.servlet.ReportDataServlet;
import com.brightspot.utils.Utils;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.util.RoutingFilter;

@RoutingFilter.Path(application = ReportTool.APPLICATION, value = ReportTool.TOOL_URL)
public class ReportsPage extends PageServlet {

    @Override
    protected String getPermissionId() {
        return ReportTool.PERMISSIONS;
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        page.writeHeader();

        page.writeStart("div",
            "class", "iframe-wrapper",
            "style", "margin: -20px -20px -60px -20px;"
        );
        {
            // load reports dashboard into an iframe
            page.writeStart("iframe",
                "id", "iframe",
                "src", Utils.addQueryParameters(ReportDataServlet.SERVLET_PATH, ReportDataServlet.PARAM_ACTION, ReportDataServlet.ACTION_INIT),
                "style", "background: #FFFFFF; border: 0; margin: 0; padding: 0;",
                "width", "100%",
                "height", "900px",
                "allowtransparency", "true"
            );
            page.writeEnd();
        }
        page.writeEnd();

        page.writeFooter();
    }
}
