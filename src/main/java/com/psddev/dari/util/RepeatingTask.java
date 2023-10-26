package com.psddev.dari.util;

import java.util.concurrent.atomic.AtomicReference;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

/**
 * Task that repeatedly runs at a predictable interval.
 */
public abstract class RepeatingTask extends Task {

    private final AtomicReference<DateTime> previousRunTime = new AtomicReference<>(new DateTime());

    public RepeatingTask(String executor, String name) {
        super(executor, name);
    }

    public RepeatingTask() {
        this(null, null);
    }

    protected abstract DateTime calculateRunTime(DateTime currentTime);

    protected DateTime every(DateTime currentTime, DateTimeFieldType unit, int offset, int interval) {
        DateTime d = currentTime.property(unit).roundFloorCopy();
        d = d.withFieldAdded(unit.getDurationType(), offset);
        return d.withField(unit, (d.get(unit) / interval) * interval);
    }

    protected DateTime everyMinute(DateTime currentTime) {
        return every(currentTime, DateTimeFieldType.minuteOfHour(), 0, 1);
    }

    protected DateTime everyHour(DateTime currentTime) {
        return every(currentTime, DateTimeFieldType.hourOfDay(), 0, 1);
    }

    protected DateTime everyDay(DateTime currentTime) {
        return every(currentTime, DateTimeFieldType.dayOfMonth(), 0, 1);
    }

    protected abstract void doRepeatingTask(DateTime runTime) throws Exception;

    @Override
    protected final void doTask() throws Exception {

        DateTime now = DateTime.now();
        DateTime previousRunTime = getPreviousRunTime().get();

        // Next run time is either NOW, if immediate run was requested OR the calculated next run time
        DateTime nextRunTime = calculateRunTime(previousRunTime);

        if (nextRunTime == null) {
            skipRunCount();
            return;
        }

        // Execute task if
        // immediate run is requested
        // OR
        // next run time is now or in the past
        // next run time is after previous run time (if applicable)
        // previous run time hasn't been modified since we retrieved it
        if (!now.isBefore(nextRunTime)
            && (previousRunTime == null || previousRunTime.isBefore(nextRunTime))
            && getPreviousRunTime().compareAndSet(previousRunTime, nextRunTime)) {

            try {
                setSafeToStop(false);
                doRepeatingTask(nextRunTime);
            } finally {
                setSafeToStop(true);
            }
        } else {
            skipRunCount();
        }
    }

    public AtomicReference<DateTime> getPreviousRunTime() {
        return previousRunTime;
    }
}
