package com.brightspot.task.expiry;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.brightspot.model.expiry.Expirable;
import com.brightspot.task.AbstractTask;
import com.brightspot.utils.TaskUtils;
import com.psddev.cms.db.Content;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.WriteOperation;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpiredContentPurgeTask extends AbstractTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredContentPurgeTask.class);

    private static final AtomicBoolean FORCE_RUN = new AtomicBoolean(false);

    private static final long DAYS_BEFORE_ARCHIVE = 30L;

    public ExpiredContentPurgeTask() {
        super(AbstractTask.EXECUTOR_NAME, ExpiredContentPurgeTask.class.getName());
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
        return everyDay(currentTime);
    }

    @Override
    protected void execute() {
        Query<Expirable> query = Query.from(Expirable.class).where(Expirable.Data.EXPIRED_PREDICATE);

        // delete expired content
        TaskUtils.asyncProcessQuery(
            Database.Static.getDefault(),
            "Delete Expired Content",
            logger()::debug,
            Expirable.class,
            query,
            Expirable::shouldDeleteAfterExpiry,
            WriteOperation.DELETE,
            5,
            5,
            200,
            true,
            null
        );

        // archive expired content
        TaskUtils.asyncProcessQuery(
            Database.Static.getDefault(),
            "Archive Expired Content",
            logger()::debug,
            Expirable.class,
            query,
            shouldArchive(),
            WriteOperation.SAVE_UNSAFELY,
            5,
            5,
            200,
            true,
            null
        );
    }

    private Function<Expirable, Boolean> shouldArchive() {
        return record -> {
            long milliseconds = Math.abs(new Date().getTime() - record.getExpiryDate().getTime());
            if (TimeUnit.DAYS.convert(milliseconds, TimeUnit.MILLISECONDS) > DAYS_BEFORE_ARCHIVE) {
                record.as(Content.ObjectModification.class).setTrash(true);
                return true;
            }
            return false;
        };
    }

    // -- Statics -- //

    public static void runTask() {
        FORCE_RUN.set(true);
    }
}
