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

/**
 * Utility class for database operations.
 *
 * <p>This class provides methods for common database operations including finding
 * records by ID, batch operations, and safe saving with fallback to unsafe save.</p>
 *
 * <p>All methods handle null inputs gracefully and return appropriate default values.</p>
 */
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
     * <p>Converts the string ID to a UUID and queries the database. Returns {@code null}
     * if the ID is blank, cannot be parsed as a UUID, or if the record is not found.</p>
     *
     * @param <T> the record type
     * @param clazz the record class
     * @param id the record ID as a string (must be a valid UUID)
     * @return the record, or {@code null} if not found or ID is invalid
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

    /**
     * Finds multiple records by their IDs.
     *
     * <p>Queries the database for all records matching the provided IDs. Returns an
     * empty list if the IDs list is null or empty. The IDs are expected to be UUIDs
     * but this method does not validate them - invalid IDs will simply not match any records.</p>
     *
     * <p>TODO: Consider validating UUID format before querying to avoid unnecessary database calls</p>
     *
     * @param <T> the record type
     * @param clazz the record class
     * @param ids the list of record IDs as strings
     * @return a list of matching records, or empty list if no IDs provided or no matches found
     */
    public static <T> List<T> findByIds(Class<T> clazz, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        return Query.from(clazz).where("_id = ?", ids).selectAll();
    }

    /**
     * Safely saves a record, falling back to unsafe save if the safe save fails.
     *
     * <p>Attempts to save the record using the standard save operation. If that fails
     * (e.g., due to validation errors or constraint violations), it falls back to
     * an unsafe save operation that bypasses some checks.</p>
     *
     * <p>FIXME: Unsafe save may bypass important validation - consider logging the reason for the fallback and reviewing why safe save is failing</p>
     * <p>TODO: Consider adding a parameter to control whether to log warnings on fallback</p>
     *
     * @param record the record to save (ignored if null)
     */
    public static void safeSave(Recordable record) {
        if (record == null) {
            return;
        }

        State state = record.getState();
        try {
            state.save();
        } catch (Exception e) {
            LOGGER.warn("Could not save record with ID {}; saving unsafely", state.getId(), e);
            state.saveUnsafely();
        }
    }

    /**
     * Opens an asynchronous write queue for batch database operations.
     *
     * <p>Creates an async queue with multiple database writers to process records
     * in parallel. The number of writers is limited to {@value #MAX_WRITER_COUNT}.
     * If writerCount is 0, it defaults to the maximum.</p>
     *
     * <p>Each writer processes records in batches of 50 and can optionally commit
     * eventually for better performance.</p>
     *
     * <p>TODO: Consider making batch size configurable instead of hardcoded to 50</p>
     *
     * @param name the name for the async writers (used in logging)
     * @param operation the write operation to perform on each record
     * @param writerCount the number of writer threads (0 to use default, max {@value #MAX_WRITER_COUNT})
     * @param eventually if {@code true}, commits are eventually consistent for better performance
     * @return the async queue that records should be added to
     */
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
