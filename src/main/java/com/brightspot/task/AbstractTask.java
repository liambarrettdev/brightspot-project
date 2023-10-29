package com.brightspot.task;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.brightspot.utils.CronUtils;
import com.brightspot.utils.TaskUtils;
import com.psddev.dari.util.RepeatingTask;
import com.psddev.dari.util.Settings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTask extends RepeatingTask {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTask.class);

    protected static final String EXECUTOR_NAME = "Custom Tasks";

    protected abstract AbstractTaskSettings getSettings();

    protected abstract AtomicBoolean executeImmediately();

    protected abstract void execute();

    private final Object initialized = new Object();

    public AbstractTask(String executor, String name) {
        super(executor, name);
    }

    public AbstractTask() {
        this(null, null);
    }

    // -- Overrides -- //

    @Override
    protected DateTime calculateRunTime(DateTime currentTime) {
        if (initialized == null) {
            return new DateTime().minus(1);
        }

        // check if task is enabled
        Boolean enabled = Optional.ofNullable(getSettings())
            .map(AbstractTaskSettings::isEnabled)
            .orElse(false);

        if (enabled) {
            // forces the task to run immediately if true
            if (executeImmediately().compareAndSet(true, false)) {
                return DateTime.now().minus(1000);
            }

            DateTime nextRunTime = Optional.ofNullable(getSettings().getCronExpression())
                .map(CronUtils::getNextExecutionTime)
                .orElse(null);

            if (nextRunTime != null) {
                return nextRunTime.minus(1000);
            }
        }

        // don't return null here as it will throw NPEs in the com.psddev.dari.util.RepeatingTask
        return currentTime.plusDays(1);
    }

    @Override
    protected void doRepeatingTask(DateTime currentTime) {
        if (!Settings.isProduction()) {
            setSafeToStop(true);
        }

        AbstractTaskSettings settings = getSettings();
        if (settings == null || !TaskUtils.isRunningOnTaskHost()) {
            return;
        }

        if (!settings.isEnabled()) {
            LOGGER.warn("[{}] is not enabled", getClass().getSimpleName());
            return;
        }

        try {
            execute();
        } catch (Exception e) {
            LOGGER.error("Exception running task", e);
        }
    }
}
