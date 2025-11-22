package com.brightspot.task;

import java.util.Optional;

import com.brightspot.utils.CronUtils;
import com.brightspot.utils.TaskUtils;
import org.joda.time.DateTime;

public abstract class AbstractCronTask extends AbstractTask {

    protected static final String EXECUTOR_NAME = "Scheduled Tasks";

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
            return;
        }

        if (!settings.isEnabled()) {
            logger().warn("[{}] is not enabled", getClass().getSimpleName());
            return;
        }

        super.doRepeatingTask(runTime);
    }

    public Boolean isBlocked() {
        return TaskUtils.isOtherTaskRunning(EXECUTOR_NAME, this.getClass().getName());
    }
}
