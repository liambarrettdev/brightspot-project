package com.brightspot.task;

import com.brightspot.utils.TaskUtils;
import com.psddev.dari.util.RepeatingTask;
import com.psddev.dari.util.Settings;
import org.joda.time.DateTime;
import org.slf4j.Logger;

public abstract class AbstractTask extends RepeatingTask {

    protected static final String EXECUTOR_NAME = "Background Tasks";

    protected abstract Logger logger();

    protected abstract Boolean runImmediately();

    protected abstract DateTime calculateNextRunTime(DateTime currentTime);

    protected abstract void execute() throws TaskExecutionException;

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
    protected void doRepeatingTask(DateTime runTime) {
        if (!Settings.isProduction()) {
            setSafeToStop(true);
        }

        if (!TaskUtils.isRunningOnTaskHost()) {
            return;
        }

        try {
            execute();
        } catch (TaskExecutionException e) {
            logger().error("Task execution failed: {}", e.getMessage(), e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger().warn("Invalid task configuration: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger().error("Unexpected error in task execution", e);
        }
    }
}
