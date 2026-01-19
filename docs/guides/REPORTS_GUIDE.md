# Reports Package

## Overview

The reports package provides a comprehensive framework for creating, displaying, and exporting data reports within the Brightspot CMS. This package enables administrators to generate customizable reports with filtering, sorting, and multiple export formats.

## Architecture

The reports system consists of several key components:

### Core Components

1. **AbstractReport** - Base class for implementing custom reports
2. **ReportTool** - CMS tool integration for managing reports
3. **ReportServlet** - Servlet handling report data requests
4. **ReportsPage** - Page servlet rendering the reports dashboard UI
5. **Filter System** - Flexible filtering mechanism with multiple filter types
6. **ReportExecutionException** - Custom exception for report errors

### Report Process

The report generation process follows these steps:

1. **Report Selection**: User selects a report from the dashboard
2. **Filter Configuration**: User configures filters for the report
3. **Query Building**: Report builds database query with applied filters
4. **Data Retrieval**: Query executed with pagination support
5. **Data Transformation**: Results transformed into table format
6. **Display/Export**: Data displayed in table or exported as CSV/email

## AbstractReport

The `AbstractReport` class provides a framework for implementing custom reports. It extends `Record` and implements `Singleton` and `DefaultGlobal`.

### Key Methods to Override

When creating a custom report, you must implement:

1. **`buildQuery(HttpServletRequest request, Boolean applyFilters)`** - Builds the database query with optional filter application
2. **`buildTableContentJson(ToolPageContext page, Query<?> query, Map<String, Object> jsonMap, Long offset, Integer length)`** - Populates table data for JSON response
3. **`writeTableCsv(ToolPageContext page, Query<?> query)`** - Writes CSV rows for export
4. **`getDefaultSortColumn()`** - Returns the index of the default sort column
5. **`getLogger()`** - Returns the logger instance for the report

### Optional Overrides

- **`getFilterFields(HttpServletRequest request)`** - Defines custom filters for the report (defaults to empty list)
- **`buildTableHeadings(HttpServletRequest request, Query<?> query)`** - Defines table column structure (defaults to empty list)
- **`getDefaultSortOrder()`** - Returns default sort order (defaults to ASC)

### Configuration Properties

#### Required Fields

- **name** (String): Display name of the report

#### Optional Fields

- **timeout** (Double): Query timeout in seconds (default: 20.0)
- **numberOfRows** (String): Default number of rows per page (default: "50", options: 25, 50, 100, 250, 500, 1000)

### Base Functionality

The base class automatically handles:

- **Table JSON Structure**: Builds DataTables-compatible JSON with pagination, sorting, and AJAX configuration
- **CSV Export**: Provides CSV formatting with proper escaping and UTF-8 BOM
- **Filter Rendering**: Renders filters as JSON or HTML
- **Email Export**: Sends report as CSV attachment via email
- **URL Generation**: Generates report data endpoints with filter parameters
- **Date Formatting**: Provides date formatting utilities
- **Common Filter Constants**: Provides `FILTER_BY_SITE` and `FILTER_BY_PUBLISH_DATE` constants

### Common Filter Constants

- `FILTER_BY_SITE` - Filter by site/market: `Site.OWNER_FIELD`
- `FILTER_BY_PUBLISH_DATE` - Filter by publish date: `Content.PUBLISH_DATE_FIELD`

### Helper Methods

- **`addColumnHeading(List<Map<String, Object>> columns, String id, String label, String className, String sortName)`** - Adds a column heading to the table
- **`writeCsvRow(ToolPageContext page, List<String> row)`** - Writes a CSV row with proper escaping
- **`getEscapedValue(Object value)`** - Escapes a value for CSV output
- **`buildPermalink(String permalink, String name, String target)`** - Builds an HTML link
- **`getStringFromRequest(HttpServletRequest request, String parameterName)`** - Gets a string parameter from request
- **`getDateFromRequest(HttpServletRequest request, String parameterName, String dateFormat)`** - Gets a date parameter from request
- **`getRowOffset(HttpServletRequest request)`** - Gets pagination offset from request
- **`getSortFieldName(HttpServletRequest request)`** - Gets sort field name from request

## ReportTool

The `ReportTool` class integrates reports into the CMS admin interface. It manages the list of available reports and provides access to the reports dashboard.

### Features

- Registers reports as a CMS tool plugin
- Manages the list of configured reports
- Provides access via `/admin/reports` URL
- Requires `area/admin/reports` permission

### Static Methods

- **`getInstance()`** - Gets the ReportTool instance
- **`get(Function<ReportTool, T> getter)`** - Gets a value from ReportTool
- **`get(Function<ReportTool, T> getter, T defaultValue)`** - Gets a value with default fallback

### Usage

Reports are configured in global settings by adding them to the `ReportTool.getReports()` list.

## ReportServlet

The `ReportServlet` class handles all report data requests at the `/_api/report/data/` endpoint.

### Actions

1. **INIT** - Generates the main reports dashboard HTML
2. **FILTER** - Generates filter HTML/JSON for the selected report
3. **REPORT** - Generates report data in JSON format or triggers CSV download/email

### Output Formats

- **JSON** - Returns report data as JSON for DataTables display
- **HTML** - Returns filter HTML
- **FILE** - Triggers CSV file download
- **EMAIL** - Emails report as CSV attachment to current user

### Request Parameters

- `id` - Report ID (required)
- `action` - Action type: INIT, FILTER, or REPORT (required)
- `output` - Output format: JSON, HTML, FILE, or EMAIL
- `start` - Row offset for pagination
- `length` - Number of rows to return
- `order[0][column]` - Sort column index
- `order[0][dir]` - Sort direction (asc/desc)
- Filter-specific parameters based on filter names

### Static Methods

- **`writeFilter(ToolPageContext page, ReportFilter filter, Query<?> query)`** - Writes filter HTML
- **`getAllValuesForField(Query<?> query, String fieldName)`** - Gets all distinct values for a field
- **`sort(List<Object> options)`** - Sorts filter options

## ReportsPage

The `ReportsPage` class is the page servlet that renders the reports dashboard within the CMS admin interface. It loads the dashboard in an iframe for seamless integration.

## Filter System

The reports package provides a flexible filtering system with multiple filter types.

### ReportFilter

Base abstract class for all filters. Provides common properties:

- **name** (String): Parameter name for the filter
- **label** (String): Display label
- **heading** (String): Optional heading text
- **defaultValue** (String): Default filter value
- **extraClass** (String): Additional CSS classes
- **onNewLine** (Boolean): Whether to render on a new line

### TypedFilter

A filter with a specific filter type implementation. Supports:

- **DateRangeFilterType** - Date range picker with from/to dates
  - `currentMonthByDefault` - Pre-fill with current month
  - `required` - Make date range required
  - `recalcAvailable` - Show recalculate button

- **DropdownFilterType** - Dropdown select with predefined options
  - Uses `DropdownOption` objects for options
  - Supports custom option labels and values

- **CheckboxFilterType** - Single checkbox filter
  - `value` - Checkbox value
  - `checkedByDefault` - Whether checked by default

### GenericFilter

Automatically generates filter options based on database field values. Supports:

- **Site Filtering** - Special handling for site/market selection
- **Enum Fields** - Automatically generates options from enum values
- **Date Fields** - Automatically creates date range picker
- **Record Fields** - Generates dropdown from related records
- **Text/Number Fields** - Generates dropdown from distinct field values

## Creating a Custom Report

### Step 1: Create the Report Class

```java
public class MyCustomReport extends AbstractReport {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCustomReport.class);
    private static final Integer DEFAULT_SORT_COLUMN = 0;
    
    @Override
    public String getDefaultNameFallback() {
        return "My Custom Report";
    }
    
    @Override
    public Query<?> buildQuery(HttpServletRequest request, Boolean applyFilters) {
        Query<MyContent> query = Query.from(MyContent.class)
            .where("* matches *")
            .timeout(getTimeout());
        
        if (applyFilters) {
            // Apply filters from request
            String filterValue = getStringFromRequest(request, "myFilter");
            if (StringUtils.isNotBlank(filterValue)) {
                query.and("myField = ?", filterValue);
            }
            
            // Apply sorting
            String sortFieldName = getSortFieldName(request);
            String sortDirection = getStringFromRequest(request, SORT_BY_DIRECTION);
            if (StringUtils.isNoneBlank(sortFieldName, sortDirection)) {
                if ("asc".equalsIgnoreCase(sortDirection)) {
                    query.sortAscending(sortFieldName);
                } else {
                    query.sortDescending(sortFieldName);
                }
            }
        }
        
        return query;
    }
    
    @Override
    public List<Map<String, Object>> buildTableHeadings(HttpServletRequest request) {
        List<Map<String, Object>> columns = new ArrayList<>();
        
        addColumnHeading(columns, "name", "Name", null, "name");
        addColumnHeading(columns, "date", "Date", null, "date");
        addColumnHeading(columns, "status", "Status", null, null);
        
        return columns;
    }
    
    @Override
    public void buildTableContentJson(
        ToolPageContext page,
        Query<?> query,
        Map<String, Object> jsonMap,
        Long offset,
        Integer length
    ) {
        List<List<String>> data = new ArrayList<>();
        
        Query<MyContent> contentQuery = Utils.uncheckedCast(query);
        PaginatedResult<MyContent> records = contentQuery
            .timeout(getTimeout())
            .select(offset, length);
        
        for (MyContent content : records.getItems()) {
            List<String> row = new ArrayList<>();
            row.add(content.getName());
            row.add(formatDate(content.getDate(), null));
            row.add(content.getStatus());
            data.add(row);
        }
        
        jsonMap.put("data", data);
        jsonMap.put("draw", page.getRequest().getParameter("draw"));
        jsonMap.put("recordsTotal", records.getCount());
        jsonMap.put("recordsFiltered", records.getCount());
    }
    
    @Override
    public void writeTableCsv(ToolPageContext page, Query<?> query) {
        Query<MyContent> contentQuery = Utils.uncheckedCast(query);
        
        for (MyContent content : contentQuery.timeout(300D).iterable(100)) {
            List<String> row = new ArrayList<>();
            row.add(content.getName());
            row.add(formatDate(content.getDate(), null));
            row.add(content.getStatus());
            writeCsvRow(page, row);
        }
    }
    
    @Override
    public Integer getDefaultSortColumn() {
        return DEFAULT_SORT_COLUMN;
    }
    
    @Override
    public Logger getLogger() {
        return LOGGER;
    }
    
    @Override
    public List<? extends ReportFilter> getFilterFields(HttpServletRequest request) {
        List<ReportFilter> filters = new ArrayList<>();
        
        TypedFilter dateFilter = new TypedFilter();
        dateFilter.setName(FILTER_BY_PUBLISH_DATE);
        dateFilter.setLabel("Date");
        dateFilter.setType(new DateRangeFilterType(true, true, true));
        filters.add(dateFilter);
        
        return filters;
    }
}
```

### Step 2: Implement Query Building

In the `buildQuery` method:
1. Create base query from your content type
2. Apply timeout using `getTimeout()`
3. Apply filters from request parameters when `applyFilters` is true
4. Apply sorting based on request parameters
5. Return the query

### Step 3: Define Table Structure

In the `buildTableHeadings` method:
1. Create list of column maps
2. Use `addColumnHeading()` to add columns
3. Specify sortable columns with `sortName` parameter
4. Return the column list

### Step 4: Populate Table Data

In the `buildTableContentJson` method:
1. Cast query to your content type
2. Use `select(offset, length)` for pagination
3. Format data into rows matching column order
4. Set `data`, `draw`, `recordsTotal`, and `recordsFiltered` in jsonMap

### Step 5: Implement CSV Export

In the `writeTableCsv` method:
1. Cast query to your content type
2. Use `iterable(batchSize)` for large datasets
3. Format data into rows matching column order
4. Use `writeCsvRow()` to write each row

### Step 6: Register the Report

Add your report instance to `ReportTool.getReports()` in global settings:
1. Navigate to global settings
2. Find ReportTool configuration
3. Add your report instance to the reports list

## PublishedContentReport

The `PublishedContentReport` class is a complete example implementation that demonstrates:

- Filtering by publish date (date range)
- Filtering by site
- Filtering by content type
- Displaying columns: Published Date, Site, Title, Type, Author, Page Views
- Sorting by date, site, or page views
- CSV export functionality

## Best Practices

1. **Always use pagination** - Use `select(offset, length)` for table data and `iterable(batchSize)` for CSV exports to handle large datasets
2. **Set appropriate timeouts** - Use `getTimeout()` for queries, consider longer timeouts for CSV exports (300 seconds)
3. **Use helper methods** - Leverage `addColumnHeading()`, `writeCsvRow()`, and other helper methods
4. **Handle null values** - Use `Optional` or null checks when mapping data
5. **Provide meaningful column names** - Use clear, descriptive labels for table columns
6. **Use appropriate filter types** - Choose the right filter type for your data (date range, dropdown, checkbox)
7. **Log errors appropriately** - Use the report's logger for error logging
8. **Test with small datasets first** - Verify report logic before running on full datasets
9. **Optimize queries** - Use appropriate indexes and query structure
10. **Handle CSV escaping** - The base class handles CSV escaping, but be aware of special characters

## Error Handling

The reports system uses `ReportExecutionException` for report-specific errors. Common error scenarios:

- **Missing parameters**: Check that required parameters (`id`, `action`) are provided
- **Query errors**: Verify query structure and field names
- **Timeout errors**: Increase timeout or optimize query
- **Data transformation errors**: Validate data types and handle null values
- **Export errors**: Check file permissions and email configuration

All errors are logged with context information to aid in debugging.

## Performance Considerations

1. **Pagination**: Always use pagination for table display to limit data transfer
2. **Query optimization**: Use appropriate indexes and query structure
3. **Timeout configuration**: Set appropriate timeouts based on query complexity
4. **Batch processing**: Use `iterable(batchSize)` for large CSV exports
5. **Caching**: Consider caching filter options for frequently accessed reports
6. **Indexing**: Ensure database fields used in filters and sorting are indexed

## Troubleshooting

### Report Not Appearing

- Verify report is added to `ReportTool.getReports()` in global settings
- Check that user has `area/admin/reports` permission
- Review ReportTool configuration

### Query Errors

- Verify query structure and field names
- Check that content type exists and is accessible
- Review query timeout settings
- Check database connection and permissions

### Filter Issues

- Verify filter field names match database fields
- Check that filter types are appropriate for field types
- Review filter option generation for GenericFilters
- Check filter parameter names in request

### CSV Export Issues

- Verify query returns data correctly
- Check file permissions for downloads
- Review CSV formatting and escaping
- Ensure batch size is appropriate for dataset size

### Email Export Issues

- Verify email service is configured
- Check that user email is set
- Review email attachment generation
- Check email service logs

## Additional Resources

- See `PublishedContentReport` implementation for a complete example
- Review filter implementations for custom filter examples
- Check DataTables documentation for table display options
- Review Brightspot CMS reports framework documentation
- Check database query optimization guides
