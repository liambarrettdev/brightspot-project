package com.brightspot.tool.task;

import java.util.function.Function;

import com.brightspot.task.auth.SessionPurgeTaskSettings;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.CmsTool;
import com.psddev.dari.db.Application;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.BeanProperty("task")
@Recordable.FieldInternalNamePrefix("settings.task.")
public class TaskSettings extends Modification<CmsTool> {

    public static final String TAB_TASK = "Tasks";

    @ToolUi.Tab(TAB_TASK)
    private String taskHost;

    @ToolUi.Tab(TAB_TASK)
    private SessionPurgeTaskSettings sessionPurgeTask;

    public String getTaskHost() {
        return taskHost;
    }

    public void setTaskHost(String taskHost) {
        this.taskHost = taskHost;
    }

    public SessionPurgeTaskSettings getSessionPurgeTask() {
        return sessionPurgeTask;
    }

    public void setSessionPurgeTask(SessionPurgeTaskSettings sessionPurgeTask) {
        this.sessionPurgeTask = sessionPurgeTask;
    }

    public static TaskSettings getInstance() {
        return Application.Static.getInstance(CmsTool.class).as(TaskSettings.class);
    }

    public static <T> T get(Function<TaskSettings, T> getter) {
        return get(getter, null);
    }

    public static <T> T get(Function<TaskSettings, T> getter, T defaultValue) {
        TaskSettings settings = getInstance();
        if (settings != null) {
            T value = getter.apply(settings);
            if (!ObjectUtils.isBlank(value)) {
                return value;
            }
        }
        return defaultValue;
    }
}
