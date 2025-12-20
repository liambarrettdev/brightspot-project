package com.brightspot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.psddev.dari.db.AsyncDatabaseWriter;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.State;
import com.psddev.dari.db.WriteOperation;
import com.psddev.dari.util.AsyncQueue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtils.class);

    public static final String INCLUDE_DRAFT_PREDICATE = "cms.content.draft = true || cms.content.draft = missing";
    public static final String INCLUDE_ARCHIVED_PREDICATE = "cms.content.trashed = true || cms.content.trashed = missing";

    private static final int MAX_WRITER_COUNT = 4;

    private DatabaseUtils() {
    }

    /**
     * Finds a record by ID.
     *
     * @param clazz the record class
     * @param id the record ID as a string
     * @return the record, or null if not found or ID is invalid
     */
    public static <T> T findById(Class<T> clazz, String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        try {
            return Query.findById(clazz, UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Could not parse string {} to UUID", id);
            return null;
        }
    }

    public static <T> List<T> findByIds(Class<T> clazz, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        return Query.from(clazz).where("_id = ?", ids).selectAll();
    }

    /**
     * Safely saves a record, falling back to unsafe save if the safe save fails.
     *
     * @param record the record to save
     */
    public static void safeSave(Recordable record) {
        if (record == null) {
            return;
        }

        State state = record.getState();
        try {
            state.save();
        } catch (Exception e) {
            LOGGER.warn("Could not save record with ID ${}; saving unsafely", state.getId(), e);
            state.saveUnsafely();
        }
    }

    public static AsyncQueue<Recordable> openWriteQueue(
        String name,
        WriteOperation operation,
        int writerCount,
        boolean eventually) {
        if (writerCount == 0) {
            writerCount = MAX_WRITER_COUNT;
        }

        AsyncQueue<Recordable> queue = new AsyncQueue<>();
        for (int i = 0; i < Math.min(writerCount, MAX_WRITER_COUNT); i++) {
            AsyncDatabaseWriter<Recordable> asyncWriter = new AsyncDatabaseWriter<>(
                name,
                queue,
                Database.Static.getDefault(),
                operation,
                50,
                eventually
            );
            asyncWriter.submit();
        }
        return queue;
    }
}
