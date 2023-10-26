package com.brightspot.servlet;

import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.task.TriggerableTask;
import com.brightspot.tool.widget.TaskWidget;
import com.psddev.cms.db.Localization;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.util.ClassFinder;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.Task;
import com.psddev.dari.util.TaskExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RoutingFilter.Path(TaskServlet.PATH)
public class TaskServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServlet.class);

    public static final String PATH = "/_api/task/run";

    // -- Overrides -- //

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        ToolPageContext page = new ToolPageContext(getServletContext(), request, response);

        Set<Class<? extends TriggerableTask>> taskClasses = ClassFinder.findConcreteClasses(TriggerableTask.class);

        writeWidget(page, taskClasses, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        ToolPageContext page = new ToolPageContext(getServletContext(), request, response);

        String taskRequestParam = request.getParameter("task");
        String statusMessage = "widget.message.failed";

        Set<Class<? extends TriggerableTask>> taskClasses = ClassFinder.findConcreteClasses(TriggerableTask.class);
        for (Class<? extends TriggerableTask> taskClass : taskClasses) {
            try {
                TriggerableTask instance = taskClass.newInstance();

                String param = instance.getTaskParam();
                if (StringUtils.isNotBlank(param) && param.equals(taskRequestParam)) {
                    statusMessage = invokeTask(instance);
                }

            } catch (Exception e) {
                LOGGER.error("Could not run" + taskRequestParam + " task", e);
            }
        }

        writeWidget(page, taskClasses, statusMessage);
    }

    // -- Utility Methods -- //

    private String invokeTask(TriggerableTask instance) {
        for (TaskExecutor executor : TaskExecutor.Static.getAll()) {
            for (Object task : executor.getTasks()) {
                if (task.getClass().equals(instance.getTaskType())) {
                    if (!((Task) task).isRunning()) {
                        instance.triggerTask();
                        return "widget.message.started";
                    } else {
                        return "widget.message.running";
                    }
                }
            }
        }

        return "widget.message.error";
    }

    public static void writeWidget(
        ToolPageContext page,
        Set<Class<? extends TriggerableTask>> taskClasses,
        String message) throws IOException {
        String widgetTitle = Localization.currentUserText(TaskWidget.class, "widget.title");
        String widgetButtonText = Localization.currentUserText(TaskWidget.class, "widget.button.text");

        page.writeStart("div", "class", "widget");
        {
            page.writeStart("h1", "class", "icon icon-tags");
            {
                page.writeHtml(widgetTitle);
            }
            page.writeEnd();

            if (StringUtils.isNotBlank(message)) {
                String widgetMessage = Localization.currentUserText(TaskWidget.class, message);

                page.writeStart("div", "class", "message message-info", "style", "margin-bottom:10px;");
                {
                    page.writeStart("p");
                    {
                        page.writeHtml(widgetMessage);
                    }
                    page.writeEnd();
                }
                page.writeEnd();
            }

            page.writeStart("form", "name", "input", "action", TaskServlet.PATH, "method", "post");
            {
                page.writeStart("p", "style", "display:block;");
                {
                    for (Class<? extends TriggerableTask> taskClass : taskClasses) {
                        try {
                            TriggerableTask instance = taskClass.newInstance();

                            String label = instance.getTaskWidgetLabel();
                            String param = instance.getTaskParam();

                            if (StringUtils.isNoneBlank(label, param)) {
                                page.writeTag("input", "type", "radio", "name", "task", "value", param);
                                page.write(label);
                                page.writeTag("br");
                            }
                        } catch (Exception e) {
                            LOGGER.warn("Error creating task entry", e);
                        }
                    }
                }
                page.writeEnd();
                page.writeTag("input", "type", "submit", "value", widgetButtonText);
            }
            page.writeEnd();
        }
        page.writeEnd();
    }
}
