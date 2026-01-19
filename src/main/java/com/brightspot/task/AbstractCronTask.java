package com.brightspot.task;

import java.util.Optional;

import com.brightspot.utils.CronUtils;
import com.brightspot.utils.TaskUtils;
import org.joda.time.DateTime;

public abstract class AbstractCronTask extends AbstractTask {

    protected static final String EXECUTOR_NAME = "Scheduled Tasks";

    /*
     * Offset to subtract from calculated next run time to account for task scheduling delays.
     * This ensures tasks run closer to their intended time despite small scheduling overhead.
     */
    private static final long RUN_TIME_OFFSET_MS = 1000L;

    protected abstract AbstractTaskSettings getSettings();

    public AbstractCronTask(String executor, String name) {
        super(executor, name);
    }

    // -- Overrides -- //

    @Override
    protected DateTime calculateNextRunTime(DateTime currentTime) {
        return Optional.ofNullable(getSettings())
            .map(AbstractTaskSettings::getCronExpression)
            .map(CronUtils::getNextExecutionTime)
            .map(nextRunTime -> nextRunTime.minus(RUN_TIME_OFFSET_MS))
            .orElse(null);
    }

    @Override
    protected void doRepeatingTask(DateTime runTime) {
        AbstractTaskSettings settings = getSettings();
        if (settings == null) {
            logger().debug("[{}] has no settings configured", getClass().getSimpleName());
            return;
        }

        if (!settings.isEnabled()) {
            logger().debug("[{}] is not enabled", getClass().getSimpleName());
            return;
        }

        super.doRepeatingTask(runTime);
    }

    public boolean isBlocked() {
        return TaskUtils.isOtherTaskRunning(EXECUTOR_NAME, this.getClass().getName());
    }
}
