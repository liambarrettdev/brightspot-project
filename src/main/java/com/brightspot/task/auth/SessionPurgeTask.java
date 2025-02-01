package com.brightspot.task.auth;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import com.brightspot.auth.Session;
import com.brightspot.task.AbstractCronTask;
import com.brightspot.task.AbstractTaskSettings;
import com.brightspot.task.MutuallyExclusiveTask;
import com.brightspot.tool.task.TaskSettings;
import com.psddev.dari.db.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionPurgeTask extends AbstractCronTask implements MutuallyExclusiveTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionPurgeTask.class);

    private static final AtomicBoolean FORCE_RUN = new AtomicBoolean(false);

    public SessionPurgeTask() {
        super(AbstractCronTask.EXECUTOR_NAME, SessionPurgeTask.class.getName());
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
    protected void execute() {
        Query<Session> query = Query.from(Session.class).where("end < ?", new Date());

        logger().debug("Found {} sessions", query.count());

        query.deleteAll();
    }

    @Override
    protected AbstractTaskSettings getSettings() {
        return TaskSettings.getInstance().getSessionPurgeTask();
    }

    @Override
    protected boolean shouldContinue() {
        if (isBlocked()) {
            skipRunCount();
            return false;
        }
        return super.shouldContinue();
    }

    // -- Statics -- //

    public static void runTask() {
        FORCE_RUN.set(true);
    }
}
