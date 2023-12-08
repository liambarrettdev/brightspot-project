package com.brightspot.task;

import com.brightspot.utils.TaskUtils;
import com.psddev.dari.util.RepeatingTask;
import com.psddev.dari.util.Settings;
import org.joda.time.DateTime;
import org.slf4j.Logger;

public abstract class AbstractTask extends RepeatingTask {

    protected static final String EXECUTOR_NAME = "Background Tasks";

    protected abstract Logger logger();

    protected abstract DateTime calculateNextRunTime(DateTime currentTime);

    protected abstract boolean runImmediately();

    protected abstract void execute();

    public AbstractTask(String executor, String name) {
        super(executor, name);
    }

    // -- Overrides -- //

    @Override
    protected DateTime calculateRunTime(DateTime currentTime) {
        if (runImmediately()) {
            return new DateTime();
        }

        return calculateNextRunTime(currentTime);
    }

    @Override
    protected void doRepeatingTask(DateTime runtTime) {
        if (!Settings.isProduction()) {
            setSafeToStop(true);
        }

        if (!TaskUtils.isRunningOnTaskHost()) {
            return;
        }

        try {
            execute();
        } catch (Exception e) {
            logger().error("Exception running task", e);
        }
    }
}
