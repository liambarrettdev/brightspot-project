package com.brightspot.report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.report.filter.ReportFilter;
import com.brightspot.servlet.ReportDataServlet;
import com.brightspot.tool.CustomGlobalSettings;
import com.brightspot.tool.DefaultGlobal;
import com.brightspot.tool.email.CustomMailMessage;
import com.brightspot.tool.email.attachment.UrlAttachment;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.db.ToolUser;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.Metric;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.Singleton;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.Task;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.slf4j.Logger;

@ToolUi.FieldDisplayOrder({
    "name",
    "getNormalizedName",
    "numberOfRows",
    "defaultQueryTimeout"
})
public abstract class AbstractReport extends Record implements
    DefaultGlobal,
    Singleton {

    public static final String FILTER_BY_SITE = Site.OWNER_FIELD;
    public static final String FILTER_BY_PUBLISH_DATE = Content.PUBLISH_DATE_FIELD;

    public static final String SORT_BY_COLUMN = "order[0][column]";
    public static final String SORT_BY_DIRECTION = "order[0][dir]";
    public static final String SORT_BY_ASCENDING = "asc";
    public static final String SORT_BY_DESCENDING = "desc";

    protected static final String REPORT_TYPE = "report-type";
    protected static final String LEFT_ALIGNMENT = "alignLeft";
    protected static final String RIGHT_ALIGNMENT = "alignRight";
    protected static final String FE_TAB = "new-fe-tab";
    protected static final String NO_RESULTS = "n/a";
    protected static final String DEFAULT_NUMBER_OF_ROWS = "50";
    protected static final Double DEFAULT_QUERY_TIMEOUT = 20.0;
    protected static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

    private String name = getName();

    private Double timeout = DEFAULT_QUERY_TIMEOUT;

    @Values({ "25", "50", "100", "250", "500", "1000" })
    private String numberOfRows = DEFAULT_NUMBER_OF_ROWS;

    @Indexed
    public String getNormalizedName() {
        return Utils.toNormalized(getName());
    }

    /**
     * Build a query to populate the report data.
     *
     * @param request the incoming request
     * @param applyFilters whether to apply user-submitted filters
     * @return data query
     */
    public abstract Query<?> buildQuery(HttpServletRequest request, Boolean applyFilters);

    /**
     * Executes the query from
     * {@link AbstractReport#buildQuery(HttpServletRequest, Boolean)} and appends results
     * to table content
     *
     * @param page the page context
     * @param query the report data query
     * @param jsonMap the base table JSON data map
     * @param offset the row offset
     * @param length the number of rows to render
     */
    public abstract void buildTableContentJson(
        ToolPageContext page,
        Query<?> query,
        Map<String, Object> jsonMap,
        Long offset,
        Integer length);

    /**
     * Executes the query from
     * {@link AbstractReport#buildQuery(HttpServletRequest, Boolean)} and write the result
     * to CSV format
     *
     * @param page the page context
     * @param query the report data query
     */
    public abstract void writeTableCsv(ToolPageContext page, Query<?> query);

    /**
     * The index of the default column to sort query results by.
     */
    public abstract Integer getDefaultSortColumn();

    /**
     * Get the logger for the report instance.
     *
     * @return the logger
     */
    public abstract Logger getLogger();

    public String getName() {
        return ObjectUtils.firstNonBlank(name, getDefaultName());
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTimeout() {
        return ObjectUtils.firstNonBlank(timeout, DEFAULT_QUERY_TIMEOUT);
    }

    public void setTimeout(Double timeout) {
        this.timeout = timeout;
    }

    public String getNumberOfRows() {
        return ObjectUtils.firstNonBlank(numberOfRows, DEFAULT_NUMBER_OF_ROWS);
    }

    public void setNumberOfRows(String numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    // -- Helper Methods -- //

    public void buildFilters(HttpServletRequest request, HttpServletResponse response, String output)
        throws IOException {
        if (output == null) {
            return;
        }

        switch (output) {
            case ReportDataServlet.OUTPUT_JSON:
                renderFiltersJson(request, response);
                break;
            case ReportDataServlet.OUTPUT_HTML:
                renderFiltersHtml(request, response);
                break;
            default:
                break;
        }
    }

    public void buildReport(HttpServletRequest request, HttpServletResponse response, String output)
        throws IOException {
        if (output == null) {
            return;
        }

        switch (output) {
            case ReportDataServlet.OUTPUT_JSON:
                // create display of the results of the report on the dashboard
                displayReport(request, response, buildQuery(request, true));
                break;
            case ReportDataServlet.OUTPUT_FILE:
                // create download of the results of the report
                downloadReport(request, response, buildQuery(request, true));
                break;
            case ReportDataServlet.OUTPUT_EMAIL:
                ToolUser currentUser = Utils.getCurrentToolUser(request);
                emailReport(getEmailAttachmentUrl(request), currentUser);
                break;
            default:
                break;
        }
    }

    public List<? extends ReportFilter> getFilterFields(HttpServletRequest request) {
        return getFilterFields();
    }

    public List<? extends ReportFilter> getFilterFields() {
        return new ArrayList<>();
    }

    public List<Map<String, Object>> buildTableHeadings(HttpServletRequest request, Query<?> query) {
        return buildTableHeadings(request);
    }

    public List<Map<String, Object>> buildTableHeadings(HttpServletRequest request) {
        return new ArrayList<>();
    }

    public String getDefaultSortOrder() {
        return SORT_BY_ASCENDING;
    }

    public String generateReportDataEndpoint(HttpServletRequest request, String output) {
        String url = Utils.addQueryParameters(ReportDataServlet.SERVLET_PATH,
            ReportDataServlet.PARAM_ID, getId(),
            ReportDataServlet.PARAM_ACTION, ReportDataServlet.ACTION_REPORT,
            ReportDataServlet.PARAM_OUTPUT, output);

        for (ReportFilter filter : getFilterFields(request)) {
            if (!ObjectUtils.isBlank(request.getParameter(filter.getName()))) {
                url = Utils.addQueryParameters(url, filter.getName(), request.getParameter(filter.getName()));
            } else {
                Date startDate = getDateFromRequest(request, filter.getName() + "_from", DEFAULT_DATE_FORMAT);
                if (startDate != null) {
                    url = Utils.addQueryParameters(url, filter.getName() + "_from", request.getParameter((filter.getName() + "_from")));
                }

                Date endDate = getDateFromRequest(request, filter.getName() + "_to", DEFAULT_DATE_FORMAT);
                if (endDate != null) {
                    url = Utils.addQueryParameters(url, filter.getName() + "_to", request.getParameter((filter.getName() + "_to")));
                }
            }
        }

        return url;
    }

    public void buildTableJson(ToolPageContext page, Query<?> query, Map<String, Object> jsonMap) {
        // Ajax Settings
        jsonMap.put("pageLength", Integer.parseInt(this.getNumberOfRows()));
        jsonMap.put("processing", "true");
        jsonMap.put("serverSide", "true");
        jsonMap.put("ajax", buildAjaxOptions(page));

        // Download url for button
        jsonMap.put("downloadUrl", generateReportDataEndpoint(page.getRequest(), ReportDataServlet.OUTPUT_FILE));
        // Email url for button
        jsonMap.put("emailUrl", generateReportDataEndpoint(page.getRequest(), ReportDataServlet.OUTPUT_EMAIL));
        // Default sort column
        jsonMap.put("order", buildDefaultSortColumn());
        // Column Headings
        jsonMap.put("columns", buildTableHeadings(page.getRequest(), query));
        // Number of entries dropdown
        jsonMap.put("lengthMenu", Arrays.asList(25, 50, 100, 250, 500, 1000));

        jsonMap.put("fixedHeader", true);
    }

    public void renderFiltersJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        List<Map<String, Object>> jsonArray = new ArrayList<>();

        List<? extends ReportFilter> filters = getFilterFields(request);
        if (!ObjectUtils.isBlank(filters)) {
            for (ReportFilter filter : filters) {
                jsonArray.add(filter.toJson(buildQuery(request, false)));
            }
        }

        response.getWriter().write(ObjectUtils.toJson(jsonArray));
    }

    public void renderFiltersHtml(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(ContentType.TEXT_HTML.getMimeType());

        ToolPageContext page = new ToolPageContext(request.getServletContext(), request, response);

        // heading
        page.writeStart("h3");
        page.write("Filters");
        page.writeEnd();

        // filters
        List<? extends ReportFilter> filters = getFilterFields(page.getRequest());
        if (!ObjectUtils.isBlank(filters)) {
            for (ReportFilter filter : filters) {
                ReportDataServlet.writeFilter(page, filter, buildQuery(page.getRequest(), false));
            }
        }

        // submit button
        page.write("<br/>");
        page.write("<br/>");
        page.writeStart("button", "id", "get-report-data-button", "class", "icon icon-filter");
        page.write("Get Data");
        page.writeEnd();
    }

    public void displayReport(HttpServletRequest request, HttpServletResponse response, Query<?> query)
        throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ToolPageContext page = new ToolPageContext(request.getServletContext(), request, response);

        Integer length = getMaxRowCount(request);
        Long offset = getRowOffset(request);

        Map<String, Object> jsonMap = new HashMap<>();
        if (offset == null) {
            buildTableJson(page, query, jsonMap);
        } else {
            buildTableContentJson(page, query, jsonMap, offset, length);
        }

        response.getWriter().write(ObjectUtils.toJson(jsonMap));
    }

    public void downloadReport(HttpServletRequest request, HttpServletResponse response, Query<?> query)
        throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + getNormalizedName() + "-data.csv");

        ToolPageContext page = new ToolPageContext(request.getServletContext(), request, response);

        List<String> headings = new ArrayList<>();
        for (Map<String, Object> heading : buildTableHeadings(request, query)) {
            headings.add(getEscapedValue(heading.get("label")));
        }

        page.putOverride(
            Recordable.class,
            (writer, object) -> writer.write(((ToolPageContext) writer).getObjectLabel(object)));
        page.putOverride(Metric.class, (writer, object) -> writer.write(Double.toString(object.getSum())));
        page.putOverride(StorageItem.class, (writer, item) -> writer.write(item.getPublicUrl()));

        page.write('\ufeff');
        page.write(String.join(",", headings));
        page.write("\r\n");

        writeTableCsv(page, query);

        response.flushBuffer();
    }

    public void emailReport(String attachmentUrl, ToolUser currentUser) throws IOException {
        if (currentUser == null) {
            throw new IOException("No request user found");
        }

        AbstractReport report = this;

        new Task() {

            @Override
            public String getName() {
                return "Email Report Task (" + currentUser.getName() + ")";
            }

            @Override
            protected void doTask() throws Exception {
                UrlAttachment attachment = new UrlAttachment();

                attachment.setName(report.getNormalizedName() + ".csv");
                attachment.setUrl(attachmentUrl);
                attachment.setMimeType("text/csv");

                new CustomMailMessage()
                    .attachments(Collections.singletonList(attachment))
                    .from(CustomGlobalSettings.get(CustomGlobalSettings::getDefaultEmail))
                    .to(currentUser.getEmail())
                    .subject("Report Request (" + report.getName() + ")")
                    .bodyHtml(currentUser.getName() + "<br><br>Please find attached your requested report download")
                    .send();
            }
        }.schedule(1.0);
    }

    /**
     * Identifies the default sort column for the datatables plugin to use
     */
    protected List<Object> buildDefaultSortColumn() {
        List<Object> orderAttributesList = new ArrayList<>();

        // If fieldNum is -1 no field has been found to sort on - so set it to 0
        // (which is what datatables expects) but don't set a direction (asc/desc)
        int fieldNum = getDefaultSortColumn();
        if (fieldNum == -1) {
            orderAttributesList.add("0");
        } else {
            orderAttributesList.add(String.valueOf(fieldNum));
            orderAttributesList.add(getDefaultSortOrder());
        }

        return Collections.singletonList(orderAttributesList);
    }

    protected void addColumnHeading(
        List<Map<String, Object>> columns,
        String id,
        String label,
        String className,
        String sortName) {
        Map<String, Object> columnMap = new HashMap<>();

        columnMap.put("id", id);
        columnMap.put("label", label);
        columnMap.put("class", className);
        columnMap.put("orderable", StringUtils.isNotBlank(sortName));
        columnMap.put("sortName", sortName);

        columns.add(columnMap);
    }

    protected void writeCsvRow(ToolPageContext page, List<String> row) {
        try {
            List<String> columns = new ArrayList<>();
            for (String columnValue : row) {
                columns.add(getEscapedValue(columnValue));
            }
            page.write(String.join(",", columns));
            page.write("\r\n");
        } catch (IOException e) {
            getLogger().error("Error writing CSV row", e);
        }
    }

    protected String getEscapedValue(Object value) throws IOException {
        String parsedValue = value == null ? "" : Jsoup.parse(value.toString()).text();
        String escapedValue = StringEscapeUtils.escapeCsv(parsedValue);
        if (escapedValue.startsWith("\"")) {
            return escapedValue;
        } else {
            return "\"" + escapedValue + "\"";
        }
    }

    protected String buildPermalink(String permalink, String name, String target) {
        StringBuilder link = new StringBuilder();

        link.append("<a class=\"cms-link\" href=\"").append(permalink).append("\"");

        if (StringUtils.isNotBlank(target)) {
            link.append(" target=\"").append(target).append("\"");
        }

        link.append(">").append(name).append("</a>");

        return link.toString();
    }

    protected String getStringFromRequest(HttpServletRequest request, String parameterName) {
        String param = request.getParameter(parameterName);

        return StringUtils.isBlank(param) ? null : param;
    }

    protected Date getDateFromRequest(HttpServletRequest request, String parameterName, String dateFormat) {
        String param = getStringFromRequest(request, parameterName);
        if (param != null) {
            try {
                return new SimpleDateFormat(dateFormat).parse(param);
            } catch (ParseException e) {
                getLogger().debug("Unable to convert {} {} to date", parameterName, param);
            }
        }
        return null;
    }

    protected Long getRowOffset(HttpServletRequest request) {
        String param = getStringFromRequest(request, "start");
        if (param != null) {
            try {
                return Long.parseLong(param);
            } catch (NumberFormatException e) {
                getLogger().debug("Unable to convert offset {} to long", param);
            }
        }
        return null;
    }

    // -- Utility Methods -- //

    private String getEmailAttachmentUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();

        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append('?');
            url.append(queryString.replace(ReportDataServlet.OUTPUT_EMAIL, ReportDataServlet.OUTPUT_FILE));
        }

        return url.toString();
    }

    private Integer getMaxRowCount(HttpServletRequest request) {
        String param = getStringFromRequest(request, "length");
        if (param != null) {
            try {
                return Integer.parseInt(param);
            } catch (NumberFormatException e) {
                getLogger().debug("Unable to convert length {} to int", param);
            }
        } else {
            return Integer.parseInt(this.getNumberOfRows());
        }
        return null;
    }

    private Map<String, Object> buildAjaxOptions(ToolPageContext page) {
        Map<String, Object> ajaxMap = new HashMap<>();
        ajaxMap.put("url", generateReportDataEndpoint(page.getRequest(), ReportDataServlet.OUTPUT_JSON));
        ajaxMap.put("type", "POST");
        return ajaxMap;
    }

    private String getDefaultName() {
        return ObjectType.getInstance(getClass()).getDisplayName();
    }

    // -- Statics -- //

    public static String formatDate(Date date, Site site) {
        return LocalizationUtils.localizeDate(date, site, DEFAULT_DATE_FORMAT);
    }

    public static DateTime startOfMonth() {
        return new DateTime().dayOfMonth().withMinimumValue();
    }

    public static DateTime endOfMonth() {
        return new DateTime().dayOfMonth().withMaximumValue();
    }
}
