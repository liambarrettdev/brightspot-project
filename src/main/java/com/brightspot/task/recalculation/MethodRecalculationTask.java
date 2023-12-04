package com.brightspot.task.recalculation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.brightspot.tool.recalculation.MethodRecalculation;
import com.brightspot.utils.TaskUtils;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.DatabaseException;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.State;
import com.psddev.dari.util.RepeatingTask;
import com.psddev.dari.util.Settings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ingests and processes {@link MethodRecalculation}s, then deletes them. Runs every minute.
 */
public class MethodRecalculationTask extends RepeatingTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodRecalculationTask.class);

    private static final AtomicBoolean FORCE_UPDATE = new AtomicBoolean(false);

    public MethodRecalculationTask() {
        super("Scheduled Tasks", MethodRecalculationTask.class.getName());
    }
    // -- Overrides -- //

    @Override
    protected DateTime calculateRunTime(DateTime currentTime) {
        if (FORCE_UPDATE.compareAndSet(true, false)) {
            return new DateTime();
        } else if (TaskUtils.isRunningOnTaskHost()) {
            return everyMinute(currentTime);
        } else {
            // Never runs because it will never get there.
            return new DateTime().plusHours(1);
        }
    }

    @Override
    protected void doRepeatingTask(DateTime runTime) throws Exception {
        if (!TaskUtils.isRunningOnTaskHost()) {
            return;
        }

        execute();
    }

    private void execute() {
        if (!Settings.isProduction()) {
            setSafeToStop(true);
        }

        Set<UUID> processed = new HashSet<>();
        Set<UUID> recalculated = new HashSet<>();
        try {
            for (MethodRecalculation recalculation : Query.from(MethodRecalculation.class)
                .where("calculationSaveDate < ?", Database.Static.getDefault().now())
                .iterable(0)) {

                LOGGER.info("Begin processing method recalculation with identifier: {}", recalculation.getIdentifier());

                try {
                    Query<Object> query = recalculation.getQuery();
                    // Unset sort to avoid leaking database resources
                    query.setSorters(Collections.emptyList());
                    query.timeout(300.0);
                    query.iterable(5)
                        .forEach(object -> {
                            if (object instanceof Recordable && recalculated.add(((Recordable) object).getState()
                                .getId())) {
                                State state = State.getInstance(object);
                                if (state != null) {
                                    try {
                                        for (String methodName : recalculation.getMethodNames()) {
                                            state.getType().getMethod(methodName).recalculate(state);
                                        }
                                    } catch (Exception e) {
                                        // could not recalculate method
                                        LOGGER.warn(e.getMessage());
                                    }
                                }
                            }
                        });
                    processed.add(recalculation.getId());
                } catch (RuntimeException e) {
                    LOGGER.error(e.getMessage());
                }

                LOGGER.info(
                    "Finished processing method recalculation with identifier: {}",
                    recalculation.getIdentifier());
            }
        } finally {
            processed.forEach(recordId -> {
                try {
                    MethodRecalculation record = Query.findById(MethodRecalculation.class, recordId);
                    if (record != null) {
                        record.delete();
                    }
                } catch (DatabaseException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            });
        }
    }

    // -- Statics -- //

    public static void forceUpdate() {
        FORCE_UPDATE.set(true);
    }
}
