package com.psddev.dari.util;

import java.util.concurrent.atomic.AtomicReference;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task that repeatedly runs at a predictable interval.
 */
public abstract class RepeatingTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatingTask.class);

    private static final AtomicReference<DateTime> LAST_ERROR = new AtomicReference<>(new DateTime(0L));

    private final AtomicReference<DateTime> previousRunTime = new AtomicReference<>(new DateTime(0L));

    protected RepeatingTask(String executor, String name) {
        super(executor, name);
    }

    protected RepeatingTask() {
        this(null, null);
    }

    protected abstract DateTime calculateRunTime(DateTime currentTime);

    /**
     * @deprecated Use {@link #every(DateTime, DateTimeFieldType, int)} instead.
     */
    @Deprecated
    protected DateTime every(DateTime currentTime, DateTimeFieldType unit, int offset, int interval) {
        DateTime d = currentTime.property(unit).roundFloorCopy();
        d = d.withFieldAdded(unit.getDurationType(), offset);
        return d.withField(unit, (d.get(unit) / interval) * interval);
    }

    protected DateTime every(DateTime currentTime, DateTimeFieldType unit, int interval) {
        DateTime d = currentTime.property(unit).roundFloorCopy();
        return d.withField(unit, (d.get(unit) / interval) * interval);
    }

    protected DateTime everyMinute(DateTime currentTime) {
        return every(currentTime, DateTimeFieldType.minuteOfHour(), 1);
    }

    protected DateTime everyHour(DateTime currentTime) {
        return every(currentTime, DateTimeFieldType.hourOfDay(), 1);
    }

    protected DateTime everyDay(DateTime currentTime) {
        return every(currentTime, DateTimeFieldType.dayOfMonth(), 1);
    }

    protected abstract void doRepeatingTask(DateTime runTime) throws Exception;

    @Override
    protected final void doTask() throws Exception {
        DateTime now = new DateTime();
        DateTime oldPrevious;
        DateTime newPrevious;

        try {
            oldPrevious = previousRunTime.get();
            newPrevious = calculateRunTime(now);

        } catch (RuntimeException e) {
            oldPrevious = null;
            newPrevious = null;

            // Only log every hour.
            if (LAST_ERROR.get().isBefore(now.minusHours(1))) {
                LOGGER.error(String.format("Can't calculate run time for [%s]!", getClass().getName()), e);
                LAST_ERROR.set(now);
            }
        }

        if (oldPrevious != null && newPrevious != null
            && !now.isBefore(newPrevious)
            && oldPrevious.isBefore(newPrevious)
            && previousRunTime.compareAndSet(oldPrevious, newPrevious)) {

            doRepeatingTask(newPrevious);

        } else {
            skipRunCount();
        }
    }
}
