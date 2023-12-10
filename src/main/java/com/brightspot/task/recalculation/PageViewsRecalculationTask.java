package com.brightspot.task.recalculation;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.brightspot.model.list.sort.analytics.PageViewsSortable;
import com.brightspot.task.AbstractTask;
import com.brightspot.utils.TaskUtils;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.WriteOperation;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageViewsRecalculationTask extends AbstractTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageViewsRecalculationTask.class);

    private static final AtomicBoolean FORCE_UPDATE = new AtomicBoolean(false);

    public PageViewsRecalculationTask() {
        super(AbstractTask.EXECUTOR_NAME, PageViewsRecalculationTask.class.getName());
    }

    // -- Overrides -- //

    @Override
    protected Logger logger() {
        return LOGGER;
    }

    @Override
    protected Boolean runImmediately() {
        return FORCE_UPDATE.compareAndSet(true, false);
    }

    @Override
    protected DateTime calculateNextRunTime(DateTime currentTime) {
        return everyHour(currentTime);
    }

    @Override
    protected void execute() {
        Function<PageViewsSortable, Boolean> processor = (obj) -> {
            PageViewsSortable.recalculateValues(obj.getState());
            return true;
        };

        TaskUtils.asyncProcessQuery(
            Database.Static.getDefault(),
            "Recalculate Page Views",
            logger()::debug,
            PageViewsSortable.class,
            Query.from(PageViewsSortable.class),
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

    public static void forceUpdate() {
        FORCE_UPDATE.set(true);
    }
}
