package com.brightspot.migration.migrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.brightspot.migration.LegacySqlMigrationContext;
import com.brightspot.migration.MigrationUtils;
import com.brightspot.utils.ValidationUtils;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Record;
import com.psddev.migration.MigrationContext;
import com.psddev.migration.sql.SqlBaseMigrator;
import com.psddev.migration.sql.SqlMigrationContext;
import org.slf4j.Logger;

/**
 * Abstract base class for migrating data from legacy SQL databases to Brightspot content types.
 *
 * <p>This class provides a framework for implementing migrators that extract data from legacy
 * SQL databases and transform it into Brightspot {@link Record} objects. Subclasses must
 * implement methods to specify the legacy table structure and provide custom migration logic.</p>
 *
 * <p>The migration process works as follows:</p>
 * <ol>
 *   <li>Query the legacy database for record IDs and timestamps</li>
 *   <li>For each ID, query the full record data from the legacy database</li>
 *   <li>Transform the legacy data into a Brightspot record using {@link #migrateLegacyData}</li>
 *   <li>Save the record to the Brightspot database</li>
 * </ol>
 *
 * <p>Subclasses should:</p>
 * <ul>
 *   <li>Specify the legacy table name via {@link #getLegacyTableName()}</li>
 *   <li>Specify the legacy column names via {@link #getLegacyColumnNames()}</li>
 *   <li>Override {@link #migrateLegacyData} to map legacy fields to the new record</li>
 *   <li>Define a {@link AbstractLegacyData} subclass with fields matching legacy columns</li>
 * </ul>
 *
 * @param <D> the legacy data type (must extend {@link AbstractLegacyData})
 * @param <T> the target record type (must extend {@link Record})
 * @see SqlBaseMigrator
 */
public abstract class AbstractBaseMigrator<D extends AbstractBaseMigrator.AbstractLegacyData, T extends Record>
    extends SqlBaseMigrator<D, T> {

    /**
     * Returns the target content type class for this migrator.
     *
     * @return the content type class
     */
    protected abstract Class<T> getContentType();

    /**
     * Returns the legacy database table name to query for data.
     *
     * @return the legacy table name
     */
    protected abstract String getLegacyTableName();

    /**
     * Returns the list of legacy table column names to retrieve during migration.
     *
     * <p>These column names will be included in the SELECT query when fetching data
     * from the legacy database. The column names should match the fields defined in
     * the {@link AbstractLegacyData} subclass.</p>
     *
     * @return the list of legacy column names
     */
    protected abstract List<String> getLegacyColumnNames();

    /**
     * Returns the logger instance for this migrator.
     *
     * @return the logger instance
     */
    protected abstract Logger log();

    /**
     * Returns the legacy database ID field name. Defaults to "id" but can be overridden if needed.
     *
     * @return the ID field name
     */
    protected String getLegacyIdField() {
        return "id";
    }

    /**
     * Transforms legacy data into a Brightspot record.
     *
     * <p>This method is called for each legacy record during migration. The base implementation
     * handles common fields such as creation/update dates, site assignment, and publisher assignment.
     * Subclasses should override this method to map legacy-specific fields to the target record.</p>
     *
     * <p>Subclasses should call {@code super.migrateLegacyData(context, record, data)} to ensure
     * common fields are properly set.</p>
     *
     * @param context the migration context containing site and publisher information
     * @param record the target record to populate
     * @param data the legacy data to migrate
     * @throws IllegalArgumentException if any parameter is null
     */
    protected void migrateLegacyData(LegacySqlMigrationContext context, T record, D data) {
        ValidationUtils.requireNonNull(context, "Context", record, "Record", data, "Legacy Data");

        Optional.ofNullable(data.created_at)
            .ifPresent(date -> record.getState().put(Content.PUBLISH_DATE_FIELD, date));

        Optional.ofNullable(data.updated_at)
            .ifPresent(date -> record.getState().put(Content.UPDATE_DATE_FIELD, date));

        Optional.ofNullable(context.getSite())
            .ifPresent(site -> record.getState().put(Site.OWNER_FIELD, site));

        Optional.ofNullable(context.getPublisher())
            .ifPresent(user -> {
                record.getState().put(Content.PUBLISH_USER_FIELD, user);
                record.getState().put(Content.UPDATE_USER_FIELD, user);
            });
    }

    /**
     * Returns the migration context class used by this migrator.
     *
     * @return the LegacySqlMigrationContext class
     */
    @Override
    public Class<? extends SqlMigrationContext> getContextClass() {
        return LegacySqlMigrationContext.class;
    }

    /**
     * Builds a SQL query to retrieve legacy record IDs and timestamps.
     *
     * <p>This query is used to paginate through legacy records. It selects the ID field
     * and converts the created_at timestamp to milliseconds for sorting purposes.</p>
     *
     * @param limit the maximum number of records to return
     * @param offset the number of records to skip
     * @param after the ID to start after (for pagination), or null to start from the beginning
     * @param legacyId a specific legacy ID to query, or null to query all records
     * @return the SQL query string
     */
    @Override
    protected String getIdsQuery(int limit, long offset, Long after, String legacyId) {
        List<String> fields = Arrays.asList(
            getLegacyIdField(),
            "UNIX_TIMESTAMP(created_at) * 1000 as created_at"
        );

        StringBuilder query = new StringBuilder("SELECT ").append(String.join(", ", fields)).append(" ");

        query.append("FROM ").append(getLegacyTableName()).append(" ");
        query.append("WHERE 1 = 1 ");

        if (after != null) {
            query.append("AND ").append(getLegacyIdField()).append(" > ").append(after).append(" ");
        }

        if (legacyId != null) {
            query.append("AND ").append(getLegacyIdField()).append(" = '").append(legacyId).append("' ");
        }

        query.append("ORDER BY ").append(getLegacyIdField()).append(" ");
        query.append("LIMIT ").append(limit).append(" ");
        query.append("OFFSET ").append(offset);

        return query.toString();
    }

    /**
     * Builds a SQL query to retrieve full legacy record data.
     *
     * <p>This query selects all columns specified by {@link #getLegacyColumnNames()} along
     * with the ID field. The results are used to populate the {@link AbstractLegacyData}
     * objects that are then transformed into Brightspot records.</p>
     *
     * @param limit the maximum number of records to return
     * @param offset the number of records to skip
     * @param after the ID to start after (for pagination), or null to start from the beginning
     * @param legacyId a specific legacy ID to query, or null to query all records
     * @return the SQL query string
     */
    @Override
    protected String getDataQuery(int limit, long offset, Long after, String legacyId) {
        List<String> fields = new ArrayList<>(Collections.singletonList(getLegacyIdField()));
        fields.addAll(getLegacyColumnNames());

        StringBuilder query = new StringBuilder("SELECT ").append(String.join(", ", fields)).append(" ");

        query.append("FROM ").append(getLegacyTableName()).append(" ");
        query.append("WHERE 1 = 1 ");

        if (after != null) {
            query.append("AND ").append(getLegacyIdField()).append(" > ").append(after).append(" ");
        }

        if (legacyId != null) {
            query.append("AND ").append(getLegacyIdField()).append(" = '").append(legacyId).append("' ");
        }

        query.append("ORDER BY ").append(getLegacyIdField()).append(" ");
        query.append("LIMIT ").append(limit).append(" ");
        query.append("OFFSET ").append(offset);

        return query.toString();
    }

    /**
     * Creates or updates a Brightspot record from legacy data.
     *
     * <p>This method finds an existing record by legacy ID, or creates a new one if it doesn't
     * exist. It then calls {@link #migrateLegacyData} to populate the record with data from
     * the legacy system.</p>
     *
     * @param context the migration context
     * @param legacyData the legacy data to migrate
     * @return the created or updated record
     * @throws RuntimeException if migration fails, with details logged
     */
    @Override
    public T createObject(MigrationContext context, D legacyData) {
        try {
            T record = MigrationUtils.findLegacyRecord(legacyData.id, getLegacyIdPrefix(context), getContentType());

            migrateLegacyData((LegacySqlMigrationContext) context, record, legacyData);

            return record;
        } catch (Exception e) {
            log().error("Failed to create record for legacy ID '{}'", legacyData.id);
            throw new RuntimeException("Error migrating content", e);
        }
    }

    /**
     * Base class for legacy data structures.
     *
     * <p>Subclasses should define fields that match the column names returned by
     * {@link #getLegacyColumnNames()}. The field names should match the legacy database
     * column names.</p>
     */
    // CHECKSTYLE:OFF
    public abstract static class AbstractLegacyData {

        int id;
        Date created_at;
        Date updated_at;
    }
    // CHECKSTYLE:ON
}
