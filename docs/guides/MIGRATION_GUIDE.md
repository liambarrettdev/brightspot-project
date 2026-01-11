# Migration Guide

## Overview

The migration functionality provides a framework for migrating data from legacy SQL databases (primarily MySQL) into Brightspot content types. The system is designed to handle large-scale data migrations with support for pagination, connection management, and data transformation.

## Architecture

The migration system consists of several key components:

### Core Components

1. **LegacySqlMigrationContext** - Manages database connections and configuration
2. **AbstractBaseMigrator** - Base class for implementing custom migrators
3. **MigrationUtils** - Utility methods for migration operations
4. **MigrationException** - Custom exception for migration errors

### Migration Process

The migration process follows these steps:

1. **Connection Setup**: Establish connection to the legacy database using configured credentials
2. **Query Legacy Data**: Query the legacy database for record IDs and timestamps
3. **Fetch Full Records**: For each ID, retrieve the complete record data from the legacy database
4. **Transform Data**: Convert legacy data into Brightspot record format
5. **Save Records**: Persist the transformed records to the Brightspot database

## LegacySqlMigrationContext

The `LegacySqlMigrationContext` class manages connections to legacy MySQL databases and provides configuration for the migration process.

### Configuration Properties

#### Required Fields

- **host** (String): The database hostname or IP address
- **port** (Integer): The database port (defaults to 3306)
- **database** (String): The name of the database to connect to
- **username** (String): Database username
- **password** (String): Database password (marked as secret in UI)

#### Optional Fields

- **site** (Site): Default site to assign migrated content
- **publisher** (ToolUser): Default publisher/user to assign to migrated content
- **parameters** (List<String>): Additional JDBC connection parameters

### Default JDBC Parameters

The context automatically initializes optimized JDBC parameters for migration operations:

- `zeroDateTimeBehavior=convertToNull` - Handles MySQL zero dates
- `removeAbandoned=false` - Keeps connections alive
- `enableQueryTimeouts=false` - Disables query timeouts for long-running migrations
- `connectTimeout=600` - 10 minute connection timeout
- `socketTimeout=600` - 10 minute socket timeout
- `initialTimeout=30` - 30 second initial timeout
- `autoReconnect=true` - Automatically reconnect on connection loss
- `useCompression=true` - Enable compression for better performance
- `cachePrepStmts=true` - Cache prepared statements
- `cacheCallableStmts=true` - Cache callable statements
- `cacheServerConfiguration=true` - Cache server configuration
- `useLocalSessionState=true` - Use local session state
- `elideSetAutoCommits=true` - Optimize auto-commit handling
- `alwaysSendSetIsolation=false` - Optimize isolation level handling

### Connection Method

The `connect()` method:
- Loads the MySQL JDBC driver (`com.mysql.jdbc.Driver`)
- Constructs the connection URL with configured parameters
- Establishes and returns a database connection
- Logs connection attempts and failures

## AbstractBaseMigrator

The `AbstractBaseMigrator` class provides a framework for implementing custom migrators. It extends `SqlBaseMigrator` and handles the common migration workflow.

### Key Methods to Override

When creating a custom migrator, you must implement:

1. **`getContentType()`** - Returns the target Brightspot content type class
2. **`getLegacyTableName()`** - Returns the legacy database table name
3. **`getLegacyColumnNames()`** - Returns the list of columns to retrieve from the legacy table
4. **`log()`** - Returns the logger instance for this migrator
5. **`getLegacyIdPrefix(MigrationContext)`** - Returns the prefix for legacy IDs (e.g., "article:")
6. **`migrateLegacyData(LegacySqlMigrationContext, T, D)`** - Transforms legacy data into Brightspot records

### Optional Overrides

- **`getLegacyIdField()`** - Returns the legacy ID field name (defaults to "id")
- **`getLabel()`** - Returns a human-readable label for the migrator

### Base Functionality

The base class automatically handles:

- **Common Field Mapping**: Creation dates, update dates, site assignment, and publisher assignment
- **Pagination**: Queries are automatically paginated to handle large datasets
- **Record Lookup**: Finds existing migrated records by legacy ID to support re-migration
- **SQL Query Generation**: Builds optimized SQL queries for ID lookup and data retrieval

### Legacy Data Structure

Each migrator must define a static inner class extending `AbstractLegacyData`:

```java
public static class LegacyData extends AbstractBaseMigrator.AbstractLegacyData {
    // Fields matching legacy database columns
    String name;
    String description;
    // ... other fields
}
```

The base class provides:
- `int id` - Legacy record ID
- `Date created_at` - Creation timestamp
- `Date updated_at` - Update timestamp

## Creating a Custom Migrator

### Step 1: Create the Migrator Class

```java
public class MyContentMigrator extends AbstractBaseMigrator<MyContentMigrator.LegacyData, MyContent> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MyContentMigrator.class);
    private static final String LEGACY_ID_PREFIX = "mycontent:";
    private static final String LEGACY_TABLE_NAME = "my_content_table";
    
    @Override
    public String getLabel() {
        return "X.X: My Content Migration";
    }
    
    @Override
    protected Class<MyContent> getContentType() {
        return MyContent.class;
    }
    
    @Override
    protected Logger log() {
        return LOGGER;
    }
    
    @Override
    public String getLegacyIdPrefix(MigrationContext context) {
        return LEGACY_ID_PREFIX;
    }
    
    @Override
    protected String getLegacyTableName() {
        return LEGACY_TABLE_NAME;
    }
    
    @Override
    protected List<String> getLegacyColumnNames() {
        return Arrays.asList("title", "description", "created_at", "updated_at");
    }
    
    @Override
    protected void migrateLegacyData(LegacySqlMigrationContext context, MyContent record, LegacyData legacyData) {
        // Call super to handle common fields
        super.migrateLegacyData(context, record, legacyData);
        
        // Map custom fields
        Optional.ofNullable(legacyData.title)
            .ifPresent(record::setDisplayName);
        
        Optional.ofNullable(legacyData.description)
            .ifPresent(record::setDescription);
    }
    
    // Legacy data structure
    public static class LegacyData extends AbstractBaseMigrator.AbstractLegacyData {
        String title;
        String description;
    }
}
```

### Step 2: Map Legacy Fields

In the `migrateLegacyData` method:
1. Always call `super.migrateLegacyData()` first to handle common fields
2. Map legacy fields to Brightspot record properties
3. Use `Optional` for null-safe field mapping
4. Handle relationships by using other migrators' `findById` methods

### Step 3: Handle Relationships

When migrating records with relationships to other migrated content:

```java
Optional.ofNullable(legacyData.categoryId)
    .map(id -> CategoryMigrator.findById(context, id))
    .ifPresent(record::setCategory);
```

## MigrationUtils

The `MigrationUtils` class provides helper methods for migration operations.

### findLegacyRecord

Finds an existing record by its legacy ID, or creates a new instance if not found.

```java
public static <R extends Record> R findLegacyRecord(
    Object id, 
    String prefix, 
    Class<R> clazz
) throws MigrationException
```

**Parameters:**
- `id`: The legacy record ID (without prefix)
- `prefix`: The prefix to use (e.g., "article:")
- `clazz`: The record class to find or create

**Returns:** An existing record with matching legacy ID, or a new instance

**Throws:** `MigrationException` if the record class cannot be instantiated

The legacy ID is stored in the record's migration metadata as: `migration.legacyId = prefix + id`

## Best Practices

1. **Always call `super.migrateLegacyData()`** - This ensures common fields are properly set
2. **Use Optional for null-safe mapping** - Prevents NullPointerExceptions
3. **Handle relationships carefully** - Use other migrators' `findById` methods for cross-references
4. **Provide meaningful labels** - Use descriptive labels in `getLabel()` for migration UI
5. **Log errors appropriately** - Use the migrator's logger for error logging
6. **Test with small datasets first** - Verify migration logic before running on full datasets
7. **Handle data validation** - Validate legacy data before mapping to prevent errors
8. **Support re-migration** - The system automatically finds existing records by legacy ID

## Error Handling

The migration system uses `MigrationException` for migration-specific errors. Common error scenarios:

- **Connection failures**: Check database credentials and network connectivity
- **SQL errors**: Verify table and column names match legacy database schema
- **Data transformation errors**: Validate data types and handle null values
- **Record creation failures**: Check Brightspot content type configuration

All errors are logged with context information to aid in debugging.

## Performance Considerations

1. **Connection pooling**: The JDBC parameters are optimized for migration workloads
2. **Pagination**: Queries are automatically paginated to handle large datasets
3. **Caching**: Prepared statements and server configuration are cached
4. **Compression**: Database compression is enabled for better network performance
5. **Timeouts**: Extended timeouts are configured for long-running migrations

## Troubleshooting

### Connection Issues

- Verify database credentials are correct
- Check network connectivity to the database server
- Ensure the MySQL JDBC driver is available on the classpath
- Review connection timeout settings if connections are timing out

### Data Mapping Issues

- Verify legacy column names match the database schema
- Check that field types in `LegacyData` match database column types
- Ensure all required fields are mapped in `migrateLegacyData`
- Review logs for specific field mapping errors

### Relationship Issues

- Ensure related content is migrated before dependent content
- Verify `findById` methods are working correctly
- Check that legacy IDs are correctly formatted with prefixes

## Additional Resources

- See individual migrator implementations for more examples
- Review the Brightspot migration framework documentation
- Check database connection logs for detailed connection information

