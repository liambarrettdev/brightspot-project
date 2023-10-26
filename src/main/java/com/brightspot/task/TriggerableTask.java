package com.brightspot.task;

import com.psddev.dari.util.StringUtils;
import com.psddev.dari.util.Task;

public interface TriggerableTask {

    default String getTaskParam() {
        return StringUtils.toNormalized(getTaskWidgetLabel());
    }

    String getTaskWidgetLabel();

    Class<? extends Task> getTaskType();

    void triggerTask();
}
