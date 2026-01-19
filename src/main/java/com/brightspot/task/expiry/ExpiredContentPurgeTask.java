package com.brightspot.task.expiry;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.brightspot.model.expiry.Expirable;
import com.brightspot.task.AbstractTask;
import com.brightspot.task.TaskExecutionException;
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

    /*
     * Number of days after expiry before content is archived (moved to trash).
     */
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
    protected boolean runImmediately() {
        return FORCE_RUN.compareAndSet(true, false);
    }

    @Override
    protected DateTime calculateNextRunTime(DateTime currentTime) {
        return everyDay(currentTime);
    }

    @Override
    protected void execute() throws TaskExecutionException {
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

    /**
     * Determines if an expired record should be archived (moved to trash).
     * Records are archived if they have been expired for more than DAYS_BEFORE_ARCHIVE days.
     *
     * @return a function that returns {@code true} if the record should be archived
     */
    private Function<Expirable, Boolean> shouldArchive() {
        return record -> {
            if (record == null) {
                return false;
            }

            Date expiryDate = record.getExpiryDate();
            if (expiryDate == null) {
                logger().warn("Record {} has null expiry date, skipping archive check", record.getState().getId());
                return false;
            }

            Date now = new Date();
            long milliseconds = now.getTime() - expiryDate.getTime();

            // Handle edge case where expiry date is in the future
            if (milliseconds < 0) {
                logger().warn("Record {} has expiry date in the future: {}, skipping archive", record.getState().getId(), expiryDate);
                return false;
            }

            long daysSinceExpiry = TimeUnit.DAYS.convert(milliseconds, TimeUnit.MILLISECONDS);

            if (daysSinceExpiry > DAYS_BEFORE_ARCHIVE) {
                record.as(Content.ObjectModification.class).setTrash(true);
                logger().debug("Marking record {} for archive (expired {} days ago)", record.getState().getId(), daysSinceExpiry);
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
