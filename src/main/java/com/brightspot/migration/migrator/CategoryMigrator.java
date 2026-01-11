package com.brightspot.migration.migrator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.brightspot.migration.LegacySqlMigrationContext;
import com.brightspot.model.category.Category;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.migration.MigrationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migrator for converting legacy category records to Brightspot Category content.
 *
 * @see Category
 * @see AbstractBaseMigrator
 */
public class CategoryMigrator extends AbstractBaseMigrator<CategoryMigrator.LegacyData, Category> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryMigrator.class);

    private static final String LEGACY_ID_PREFIX = "category:";
    private static final String LEGACY_TABLE_NAME = "categories";

    /**
     * Returns a human-readable label for this migrator.
     */
    @Override
    public String getLabel() {
        return "1.1: Category Migration";
    }

    /**
     * Returns the Category content type class.
     */
    @Override
    protected Class<Category> getContentType() {
        return Category.class;
    }

    /**
     * Returns the logger instance for this migrator.
     */
    @Override
    protected Logger log() {
        return LOGGER;
    }

    /**
     * Returns the prefix used for legacy category IDs.
     *
     * @param context the migration context (not used in this implementation)
     */
    @Override
    public String getLegacyIdPrefix(MigrationContext context) {
        return LEGACY_ID_PREFIX;
    }

    /**
     * Returns the legacy database table name for categories.
     */
    @Override
    protected String getLegacyTableName() {
        return LEGACY_TABLE_NAME;
    }

    /**
     * Returns the list of legacy column names to retrieve.
     */
    @Override
    protected List<String> getLegacyColumnNames() {
        return Arrays.asList("name", "created_at", "updated_at");
    }

    /**
     * Migrates legacy category data to a Brightspot Category record.
     *
     * @param context the migration context
     * @param record the target Category record to populate
     * @param legacyData the legacy category data
     */
    @Override
    protected void migrateLegacyData(LegacySqlMigrationContext context, Category record, LegacyData legacyData) {
        super.migrateLegacyData(context, record, legacyData);

        Optional.of(legacyData)
            .map(data -> data.name)
            .ifPresent(record::setDisplayName);
    }

    /**
     * Finds a Category by its legacy ID.
     *
     * <p>This utility method searches for a Category that was migrated from a legacy
     * category with the given ID. The search uses the migration context to look up
     * the record by its legacy ID prefix.</p>
     *
     * @param context the migration context to use for the lookup
     * @param id the legacy category ID (without the "category:" prefix)
     * @return the found Category, or null if the context or ID is blank, or if no
     *         matching category is found
     */
    public static Category findById(MigrationContext context, String id) {
        if (ObjectUtils.isBlank(context) || ObjectUtils.isBlank(id)) {
            LOGGER.debug("No migration context or id provided");
            return null;
        }
        return context.findReferenceByLegacyId(CategoryMigrator.class, Category.class, id);
    }

    // CHECKSTYLE:OFF
    public static class LegacyData extends AbstractBaseMigrator.AbstractLegacyData {
        String name;
    }
    // CHECKSTYLE:ON
}
