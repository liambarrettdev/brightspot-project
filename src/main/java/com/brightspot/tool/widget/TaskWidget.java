package com.brightspot.tool.widget;

import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;

import com.brightspot.servlet.TaskServlet;
import com.brightspot.task.TriggerableTask;
import com.psddev.cms.tool.Dashboard;
import com.psddev.cms.tool.DefaultDashboardWidget;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.util.ClassFinder;

public class TaskWidget extends DefaultDashboardWidget {

    // -- Overrides -- //

    @Override
    public int getColumnIndex() {
        return 1;
    }

    @Override
    public int getWidgetIndex() {
        return 2;
    }

    @Override
    public void writeHtml(ToolPageContext page, Dashboard dashboard) throws IOException, ServletException {
        Set<Class<? extends TriggerableTask>> taskClasses = ClassFinder.findConcreteClasses(TriggerableTask.class);

        TaskServlet.writeWidget(page, taskClasses, null);
    }
}
