package com.brightspot.task.expiry;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.brightspot.model.expiry.Expirable;
import com.brightspot.task.AbstractTask;
import com.brightspot.utils.TaskUtils;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.WriteOperation;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Re-save {@link Expirable} objects whose expired flag needs to be updated in the database.
 */
public class ExpiredContentUpdateTask extends AbstractTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredContentUpdateTask.class);

    private static final AtomicBoolean FORCE_RUN = new AtomicBoolean(false);

    public ExpiredContentUpdateTask() {
        super(AbstractTask.EXECUTOR_NAME, ExpiredContentUpdateTask.class.getName());
    }

    // -- Overrides -- //

    @Override
    protected Logger logger() {
        return LOGGER;
    }

    @Override
    protected Boolean runImmediately() {
        return FORCE_RUN.compareAndSet(true, false);
    }

    @Override
    protected DateTime calculateNextRunTime(DateTime currentTime) {
        return everyHour(currentTime);
    }

    @Override
    protected void execute() {
        // only save records whose calculated value is not equal to the saved value.
        Function<Expirable, Boolean> processor = (obj) -> {
            Expirable.Data expirableData = obj.as(Expirable.Data.class);

            if (expirableData == null) {
                return false;
            }

            boolean actualValue = Boolean.TRUE.equals(expirableData.getOriginalObject().isExpired());
            boolean currentValue = expirableData.isExpired();

            if (currentValue != actualValue) {
                expirableData.setExpired(actualValue);
                return true;
            }

            return false;
        };

        TaskUtils.asyncProcessQuery(
            Database.Static.getDefault(),
            "Update Expired Content",
            logger()::debug,
            Expirable.class,
            Query.from(Expirable.class).where(Expirable.Data.NOT_EXPIRED_PREDICATE),
            processor,
            WriteOperation.SAVE_UNSAFELY,
            5,
            5,
            200,
            true,
            null
        );
    }

    // -- Statics -- //

    public static void runTask() {
        FORCE_RUN.set(true);
    }
}
