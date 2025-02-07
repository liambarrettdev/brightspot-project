package com.brightspot.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import com.brightspot.model.list.sort.analytics.PageViewsSortable;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.creativework.AbstractCreativeWorkPage;
import com.brightspot.model.promo.Promotable;
import com.brightspot.report.filter.DateRangeFilterType;
import com.brightspot.report.filter.DropdownFilterType;
import com.brightspot.report.filter.DropdownOption;
import com.brightspot.report.filter.GenericFilter;
import com.brightspot.report.filter.ReportFilter;
import com.brightspot.report.filter.TypedFilter;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.PaginatedResult;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishedContentReport extends AbstractReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishedContentReport.class);

    /*
     * filter fields
     */
    private static final String FILTER_BY_TYPE = "_type";

    /*
     * sort fields
     */
    private static final String SORT_BY_DATE = FILTER_BY_PUBLISH_DATE;
    private static final String SORT_BY_SITE = FILTER_BY_SITE;
    private static final String SORT_BY_PAGE_VIEWS = PageViewsSortable.Data.SORT_FIELD;
    private static final Integer SORT_BY_DATE_INDEX = 0;

    /*
     * general constants
     */
    private static final String CONTENT_PREDICATE = "groups = " + AbstractCreativeWorkPage.INTERNAL_NAME
        + " && internalName != " + AbstractCreativeWorkPage.INTERNAL_NAME
        + " && isAbstract = false";

    /*
     * configuration
     */
    @Where(CONTENT_PREDICATE)
    private Set<ObjectType> types;

    public Set<ObjectType> getTypes() {
        if (types == null) {
            types = new HashSet<>();
        }
        return types;
    }

    public void setTypes(Set<ObjectType> types) {
        this.types = types;
    }

    // -- Overrides -- //

    @Override
    public List<? extends ReportFilter> getFilterFields(HttpServletRequest request) {
        // field types
        GenericFilter genericFilter;
        TypedFilter typedFilter;
        DropdownFilterType dropdown;
        List<DropdownOption> dropdownOptions;

        // add fields
        List<ReportFilter> fields = new ArrayList<>();

        typedFilter = new TypedFilter();
        typedFilter.setOnNewLine(false);
        typedFilter.setLabel("Date");
        typedFilter.setName(FILTER_BY_PUBLISH_DATE);
        typedFilter.setType(new DateRangeFilterType(true, true, true));
        fields.add(typedFilter);

        genericFilter = new GenericFilter();
        genericFilter.setOnNewLine(false);
        genericFilter.setLabel("Site");
        genericFilter.setName(FILTER_BY_SITE);
        genericFilter.setOnNewLine(true);
        fields.add(genericFilter);

        typedFilter = new TypedFilter();
        typedFilter.setOnNewLine(false);
        typedFilter.setLabel("Type");
        typedFilter.setName(FILTER_BY_TYPE);
        dropdownOptions = new LinkedList<>();
        dropdownOptions.add(new DropdownOption("All", ""));
        for (ObjectType type : getTypes()) {
            dropdownOptions.add(new DropdownOption(type.getDisplayName(), type.getId().toString()));
        }
        dropdown = new DropdownFilterType(dropdownOptions);
        typedFilter.setType(dropdown);
        fields.add(typedFilter);

        return fields;
    }

    @Override
    public Query<?> buildQuery(HttpServletRequest request, Boolean applyFilters) {
        // request parameters
        Date filterStart = getDateFromRequest(request, FILTER_BY_PUBLISH_DATE + "_from", DEFAULT_DATE_FORMAT);
        Date filterEnd = getDateFromRequest(request, FILTER_BY_PUBLISH_DATE + "_to", DEFAULT_DATE_FORMAT);
        String filterSite = getStringFromRequest(request, FILTER_BY_SITE);
        String filterType = getStringFromRequest(request, FILTER_BY_TYPE);

        // build query
        Query<AbstractCreativeWorkPage> query = Query.from(AbstractCreativeWorkPage.class).where("* matches *").timeout(getTimeout());

        // set date range
        if (!ObjectUtils.isBlank(filterStart)) {
            query.and(String.format("%s >= ?", FILTER_BY_PUBLISH_DATE), filterStart.getTime());
        } else {
            query.and(String.format("%s >= ?", FILTER_BY_PUBLISH_DATE), startOfMonth().getMillis());
        }

        if (!ObjectUtils.isBlank(filterEnd)) {
            query.and(
                String.format("%s <= ?", FILTER_BY_PUBLISH_DATE),
                new DateTime(filterEnd.getTime()).plusDays(1).getMillis());
        } else {
            query.and(String.format("%s <= ?", FILTER_BY_PUBLISH_DATE), endOfMonth().getMillis());
        }

        if (applyFilters) {
            // apply filters
            if (StringUtils.isNotBlank(filterSite)) {
                query.and(String.format("%s = ?", FILTER_BY_SITE), filterSite);
            }

            if (StringUtils.isNotBlank(filterType)) {
                query.and(String.format("%s = ?", FILTER_BY_TYPE), filterType);
            }

            // apply sorts
            String sortDirection = getStringFromRequest(request, SORT_BY_DIRECTION);
            String sortFieldName = getSortFieldName(request);
            if (StringUtils.isNoneBlank(sortDirection, sortFieldName)) {
                query.setSorters(new ArrayList<>());

                switch (SortOrder.fromValue(sortDirection)) {
                    case ASC:
                        query.sortAscending(sortFieldName);
                        break;
                    case DESC:
                    default:
                        query.sortDescending(sortFieldName);
                        break;
                }
            }
        }

        return query;
    }

    @Override
    public List<Map<String, Object>> buildTableHeadings(HttpServletRequest request) {
        List<Map<String, Object>> columns = new ArrayList<>();

        addColumnHeading(columns, "date", "Published Date", null, SORT_BY_DATE);
        addColumnHeading(columns, "site", "Site", null, SORT_BY_SITE);
        addColumnHeading(columns, "title", "Title", null, null);
        addColumnHeading(columns, "type", "Type", null, null);
        addColumnHeading(columns, "author", "Author", null, null);
        addColumnHeading(columns, "views", "Page Views", null, SORT_BY_PAGE_VIEWS);

        return columns;
    }

    @Override
    public void buildTableContentJson(ToolPageContext page, Query<?> query, Map<String, Object> jsonMap, Long offset, Integer length) {
        List<List<String>> data = new ArrayList<>();

        Query<AbstractCreativeWorkPage> contentQuery = Utils.uncheckedCast(query);
        PaginatedResult<AbstractCreativeWorkPage> records = contentQuery.timeout(getTimeout()).select(offset, length);

        for (AbstractCreativeWorkPage content : records.getItems()) {
            if (!ObjectUtils.isBlank(content)) {
                data.add(createTableRowData(content));
            }
        }

        jsonMap.put("data", data);
        jsonMap.put("draw", page.getRequest().getParameter("draw"));
        jsonMap.put("recordsTotal", records.getCount());
        jsonMap.put("recordsFiltered", records.getCount());
    }

    @Override
    public void writeTableCsv(ToolPageContext page, Query<?> query) {
        Query<AbstractCreativeWorkPage> contentQuery = Utils.uncheckedCast(query);

        for (AbstractCreativeWorkPage content : contentQuery.timeout(300D).iterable(100)) {
            if (!ObjectUtils.isBlank(content)) {
                writeCsvRow(page, createTableRowData(content));
            }
        }
    }

    @Override
    public Integer getDefaultSortColumn() {
        return SORT_BY_DATE_INDEX;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    // -- Utility Methods -- //

    private List<String> createTableRowData(AbstractCreativeWorkPage content) {
        List<String> columnData = new ArrayList<>();

        // published date
        columnData.add(Optional.ofNullable(content)
            .map(Content::getPublishDate)
            .map(date -> formatDate(date, null))
            .orElse(NO_RESULTS));

        // site
        columnData.add(Optional.ofNullable(content)
            .map(AbstractPage::getSiteOwner)
            .map(Site::getName)
            .orElse(NO_RESULTS));

        // title
        columnData.add(Optional.ofNullable(content)
            .map(Promotable::getPromoTitle)
            .orElse(NO_RESULTS));

        // type
        columnData.add(Optional.ofNullable(content)
            .map(Promotable::getPromotableType)
            .orElse(NO_RESULTS));

        // author
        columnData.add(Optional.ofNullable(content)
            .map(Promotable::getPromotableAuthor)
            .orElse(NO_RESULTS));

        // page views
        columnData.add(Optional.ofNullable(content)
            .map(AbstractCreativeWorkPage::getPageViews)
            .map(String::valueOf)
            .orElse(NO_RESULTS));

        return columnData;
    }
}
