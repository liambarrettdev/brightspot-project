package com.brightspot.task.recalculation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.brightspot.task.AbstractTask;
import com.brightspot.tool.recalculation.MethodRecalculation;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.DatabaseException;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.State;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ingests and processes {@link MethodRecalculation}s, then deletes them. Runs every minute.
 */
public class MethodRecalculationTask extends AbstractTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodRecalculationTask.class);

    private static final AtomicBoolean FORCE_RUN = new AtomicBoolean(false);

    public MethodRecalculationTask() {
        super(AbstractTask.EXECUTOR_NAME, MethodRecalculationTask.class.getName());
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
        return everyMinute(currentTime);
    }

    @Override
    protected void execute() {
        Set<UUID> processed = new HashSet<>();
        Set<UUID> recalculated = new HashSet<>();

        try {
            for (MethodRecalculation method : Query.from(MethodRecalculation.class)
                .where("calculationSaveDate < ?", Database.Static.getDefault().now())
                .iterable(0)) {

                UUID methodId = method.getId();
                String identifier = method.getIdentifier();

                logger().info("Begin processing method recalculation with identifier: {}", identifier);

                try {Query<Object> query = method.getQuery();
                    if (query == null) {
                        logger().warn("Method recalculation {} has null query, skipping", identifier);
                        processed.add(methodId);
                        continue;
                    }

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
                                        for (String methodName : method.getMethodNames()) {
                                            state.getType().getMethod(methodName).recalculate(state);
                                        }
                                    } catch (Exception e) {
                                        // could not recalculate method
                                        logger().warn(e.getMessage());
                                    }
                                }
                            }
                        });
                    processed.add(method.getId());
                } catch (RuntimeException e) {
                    logger().error("Runtime exception processing method recalculation with identifier: {}", identifier, e);
                    // Don't add to processed set so it can be retried
                }

                logger().info("Finished processing method recalculation with identifier: {}", method.getIdentifier());
            }
        } finally {
            // Clean up processed method recalculations
            processed.forEach(recordId -> {
                try {
                    MethodRecalculation record = Query.findById(MethodRecalculation.class, recordId);
                    if (record != null) {
                        record.delete();
                        logger().debug("Deleted processed method recalculation {}", recordId);
                    }
                } catch (DatabaseException e) {
                    logger().warn("Failed to delete method recalculation {}: {}", recordId, e.getMessage(), e);
                }
            });
        }
    }

    // -- Statics -- //

    public static void runTask() {
        FORCE_RUN.set(true);
    }
}
