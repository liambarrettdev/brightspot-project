package com.brightspot.task;

import com.brightspot.utils.Utils;
import com.psddev.dari.util.Task;

public interface TriggerableTask {

    default String getTaskName() {
        return Utils.toNormalized(getTaskWidgetLabel());
    }

    String getTaskWidgetLabel();

    Class<? extends Task> getTaskType();

    void triggerTask();
}
