package com.brightspot.task.auth;

import com.brightspot.task.AbstractTaskSettings;
import com.brightspot.task.TriggerableTask;
import com.psddev.dari.util.Task;

public class SessionPurgeTaskSettings extends AbstractTaskSettings implements TriggerableTask {

    private static final String DEFAULT_CRON = "0 0 0/1 1/1 * ?"; // run every hour

    // -- Overrides -- //

    @Override
    public String getCronExpressionFallback() {
        return DEFAULT_CRON;
    }

    // TriggerableTask

    @Override
    public String getTaskWidgetLabel() {
        return "Purge Session Data";
    }

    @Override
    public Class<? extends Task> getTaskType() {
        return SessionPurgeTask.class;
    }

    @Override
    public void triggerTask() {
        SessionPurgeTask.forceUpdate();
    }
}
