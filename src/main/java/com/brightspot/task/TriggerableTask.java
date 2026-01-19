package com.brightspot.task;

import com.brightspot.utils.Utils;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.Task;

public interface TriggerableTask extends Recordable {

    default String getTaskName() {
        return Utils.toNormalized(getTaskWidgetLabel());
    }

    default boolean isTask(Object object) {
        if (object == null) {
            return false;
        }
        return object.getClass() == getTaskClass();
    }

    String getTaskWidgetLabel();

    Class<? extends Task> getTaskClass();

    void run();
}
