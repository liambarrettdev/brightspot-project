package com.brightspot.migration;

import com.brightspot.exception.MigrationException;
import com.brightspot.utils.Utils;
import com.brightspot.utils.ValidationUtils;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;

/**
 * Utility class providing helper methods for migration operations.
 */
public final class MigrationUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MigrationUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Finds an existing record by its legacy ID, or creates a new instance if not found.
     *
     * <p>This method searches for a record with the specified legacy ID (prefixed with the
     * given prefix). If no record is found, it creates a new instance of the specified class.
     * This is useful during migration when you need to either update an existing migrated
     * record or create a new one.</p>
     *
     * <p>The legacy ID is stored in the record's migration metadata using the format:
     * {@code migration.legacyId = prefix + id}</p>
     *
     * @param <R> the record type
     * @param id the legacy record ID (without prefix)
     * @param prefix the prefix to use when constructing the full legacy ID (e.g., "article:")
     * @param clazz the class of the record to find or create
     * @return an existing record with the matching legacy ID, or a new instance if not found
     * @throws MigrationException if the record class cannot be instantiated
     * @throws IllegalArgumentException if any parameter is null
     */
    public static <R extends Record> R findLegacyRecord(Object id, String prefix, Class<R> clazz) throws MigrationException {
        ValidationUtils.requireNonNull(id, "ID", prefix, "Prefix", clazz, "Class");

        Record record = Query.from(clazz).where("migration.legacyId = ?", prefix + id).first();
        if (record == null) {
            try {
                record = clazz.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new MigrationException("Could not instantiate migration class: " + clazz.getName(), e);
            }
        }

        return Utils.uncheckedCast(record);
    }
}
