package com.brightspot.task.auth;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import com.brightspot.auth.Session;
import com.brightspot.task.AbstractTask;
import com.brightspot.task.AbstractTaskSettings;
import com.brightspot.task.MutuallyExclusiveTask;
import com.brightspot.tool.task.TaskSettings;
import com.brightspot.utils.TaskUtils;
import com.brightspot.utils.Utils;
import com.psddev.dari.db.Query;

public class SessionPurgeTask extends AbstractTask implements MutuallyExclusiveTask {

    protected static final AtomicBoolean FORCE_RUN = new AtomicBoolean(false);

    public SessionPurgeTask() {
        super(EXECUTOR_NAME, getTaskName());
    }

    // -- Overrides -- //

    @Override
    protected boolean shouldContinue() {
        if (isBlocked()) {
            skipRunCount();
            return false;
        }
        return super.shouldContinue();
    }

    @Override
    public Boolean isBlocked() {
        return TaskUtils.isOtherTaskRunning(EXECUTOR_NAME, getTaskName());
    }

    @Override
    protected AbstractTaskSettings getSettings() {
        return TaskSettings.getInstance().getSessionPurgeTask();
    }

    @Override
    protected AtomicBoolean executeImmediately() {
        return FORCE_RUN;
    }

    @Override
    protected void execute() {
        Utils.sleep(30000);

        Query<Session> query = Query.from(Session.class).where("end < ?", new Date());

        LOGGER.debug("Found {} sessions", query.count());

        query.deleteAll();
    }

    protected static String getTaskName() {
        return SessionPurgeTask.class.getName();
    }
}
