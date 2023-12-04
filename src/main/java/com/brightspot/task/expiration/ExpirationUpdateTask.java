package com.brightspot.task.expiration;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.brightspot.model.event.Event;
import com.brightspot.model.expiry.Expirable;
import com.brightspot.utils.TaskUtils;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.WriteOperation;
import com.psddev.dari.util.RepeatingTask;
import com.psddev.dari.util.Settings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Re-save {@link Expirable} objects whose expired flag needs to be updated in the database.
 */
public class ExpirationUpdateTask extends RepeatingTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpirationUpdateTask.class);

    private static final AtomicBoolean FORCE_UPDATE = new AtomicBoolean(false);

    public ExpirationUpdateTask() {
        super("Scheduled Tasks", ExpirationUpdateTask.class.getName());
    }

    // -- Overrides -- //

    @Override
    protected DateTime calculateRunTime(DateTime currentTime) {
        if (FORCE_UPDATE.compareAndSet(true, false)) {
            return new DateTime();
        } else if (TaskUtils.isRunningOnTaskHost()) {
            return everyHour(currentTime);
        } else {
            // Never runs because it will never get there.
            return new DateTime().plusHours(1);
        }
    }

    @Override
    protected void doRepeatingTask(DateTime dateTime) throws Exception {
        if (!TaskUtils.isRunningOnTaskHost()) {
            return;
        }

        execute();
    }

    protected void execute() {
        if (!Settings.isProduction()) {
            setSafeToStop(true);
        }

        Runnable runnable = createRunnable(Event.class);

        runnable.run();
    }

    private <T extends Recordable> Runnable createRunnable(Class<T> expirableClass) {
        // only save records whose calculated value is not equal to the saved value.
        Function<T, Boolean> processor = (obj) -> {
            Expirable expirable = obj.as(Expirable.class);
            Expirable.Data expirableData = obj.as(Expirable.Data.class);

            if (expirable == null || expirableData == null) {
                return false;
            }

            boolean calculatedValue = Boolean.TRUE.equals(expirable.isExpired());
            boolean currentValue = expirableData.isExpired();

            if (currentValue != calculatedValue) {
                expirableData.setExpired(calculatedValue);
                return true;
            }

            return false;
        };

        return () ->
            TaskUtils.asyncProcessQuery(
                Database.Static.getDefault(),
                "Update Expired Content",
                LOGGER::info,
                expirableClass,
                Query.from(expirableClass).where(Expirable.Data.EXPIRED_PREDICATE),
                processor,
                WriteOperation.SAVE_UNSAFELY,
                5,
                5,
                200,
                true,
                null);
    }

    // -- Statics -- //

    public static void forceUpdate() {
        FORCE_UPDATE.set(true);
    }
}
