package com.brightspot.migration.migrator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.brightspot.migration.LegacySqlMigrationContext;
import com.brightspot.model.article.Article;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.migration.MigrationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migrator for converting legacy article records to Brightspot Article content.
 *
 * @see Article
 * @see AbstractBaseMigrator
 */
public class ArticleMigrator extends AbstractBaseMigrator<ArticleMigrator.LegacyData, Article> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleMigrator.class);

    private static final String LEGACY_ID_PREFIX = "article:";
    private static final String LEGACY_TABLE_NAME = "articles";

    /**
     * Returns a human-readable label for this migrator.
     */
    @Override
    public String getLabel() {
        return "3.1: Article Migration";
    }

    /**
     * Returns the Article content type class.
     */
    @Override
    protected Class<Article> getContentType() {
        return Article.class;
    }

    /**
     * Returns the logger instance for this migrator.
     */
    @Override
    protected Logger log() {
        return LOGGER;
    }

    /**
     * Returns the prefix used for legacy article IDs.
     */
    @Override
    public String getLegacyIdPrefix(MigrationContext context) {
        return LEGACY_ID_PREFIX;
    }

    /**
     * Returns the legacy database table name for articles.
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
        return Arrays.asList("title", "author", "created_at", "updated_at");
    }

    /**
     * Migrates legacy article data to a Brightspot Article record.
     *
     * @param context the migration context
     * @param article the target Article record to populate
     * @param legacyData the legacy article data
     */
    @Override
    protected void migrateLegacyData(LegacySqlMigrationContext context, Article article, LegacyData legacyData) {
        super.migrateLegacyData(context, article, legacyData);

        Optional.of(legacyData)
            .map(data -> data.title)
            .ifPresent(article::setDisplayName);

        Optional.of(legacyData)
            .map(data -> data.categoryId)
            .map(id -> CategoryMigrator.findById(context, id))
            .ifPresent(article.asCategoryData()::setCategory);
    }

    /**
     * Finds an Article by its legacy ID.
     *
     * <p>This utility method searches for an Article that was migrated from a legacy
     * article with the given ID. The search uses the migration context to look up
     * the record by its legacy ID prefix.</p>
     *
     * @param context the migration context to use for the lookup
     * @param id the legacy article ID (without the "article:" prefix)
     * @return the found Article, or null if the context or ID is blank, or if no
     *         matching article is found
     */
    public static Article findById(MigrationContext context, String id) {
        if (ObjectUtils.isBlank(context) || ObjectUtils.isBlank(id)) {
            LOGGER.debug("No migration context or id provided");
            return null;
        }
        return context.findReferenceByLegacyId(ArticleMigrator.class, Article.class, id);
    }

    // CHECKSTYLE:OFF
    public static class LegacyData extends AbstractBaseMigrator.AbstractLegacyData {
        String title;
        String author;
        String body;
        String categoryId;
    }
    // CHECKSTYLE:ON
}
