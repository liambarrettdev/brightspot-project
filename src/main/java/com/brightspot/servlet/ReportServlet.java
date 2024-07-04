package com.brightspot.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.CollectionUtils;
import com.brightspot.report.AbstractReport;
import com.brightspot.report.filter.CheckboxFilterType;
import com.brightspot.report.filter.DateRangeFilterType;
import com.brightspot.report.filter.DropdownFilterType;
import com.brightspot.report.filter.DropdownOption;
import com.brightspot.report.filter.GenericFilter;
import com.brightspot.report.filter.ReportFilter;
import com.brightspot.report.filter.TypedFilter;
import com.brightspot.tool.report.ReportTool;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUser;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.Grouping;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.RoutingFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RoutingFilter.Path(ReportServlet.SERVLET_PATH)
public class ReportServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServlet.class);

    public static final String SERVLET_PATH = "/_api/report/data/";

    public static final String PARAM_ID = "id";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_OUTPUT = "output";

    public static final String ACTION_INIT = "init";
    public static final String ACTION_FILTERS = "filters";
    public static final String ACTION_REPORT = "report";

    public static final String OUTPUT_EMAIL = "email";
    public static final String OUTPUT_HTML = "html";
    public static final String OUTPUT_JSON = "json";
    public static final String OUTPUT_FILE = "file";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setStatus(HttpServletResponse.SC_OK);

        String action = request.getParameter(PARAM_ACTION);
        AbstractReport selectedReport = Query.from(AbstractReport.class)
                .where("id = ?", request.getParameter(PARAM_ID))
                .first();

        if (action == null) {
            return;
        }

        switch (action) {
            case ACTION_INIT:
                // generate main dashboard HTML
                createReportDashboard(request, response);
                break;
            case ACTION_FILTERS:
                // generate report filters HTML for the selected report
                if (!ObjectUtils.isBlank(selectedReport)) {
                    selectedReport.buildFilters(request, response, request.getParameter(PARAM_OUTPUT));
                }
                break;
            case ACTION_REPORT:
                // generate report HTML or trigger CSV download for the selected report
                if (!ObjectUtils.isBlank(selectedReport)) {
                    selectedReport.buildReport(request, response, request.getParameter(PARAM_OUTPUT));
                }
                break;
            default:
                break;
        }
    }

    private void createReportDashboard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(ContentType.TEXT_HTML.getMimeType());

        ToolUser currentUser = Utils.getCurrentToolUser(request);
        if (!ObjectUtils.isBlank(currentUser)) {
            ToolPageContext page = new ToolPageContext(request.getServletContext(), request, response);

            writeStylesheetLink(page, "/cms/style/v3.min.css");
            writeStylesheetLink(page, "/_resource/cms/datatables/datatables.min.css");
            writeStylesheetLink(page, "/_resource/cms/datepicker/bootstrap-datepicker-standalone.css");
            writeStylesheetLink(page, "/_resource/cms/report/reports.css");

            writeScript(page, "/_resource/cms/jquery/jquery-2.2.4.min.js");
            writeScript(page, "/_resource/cms/datatables/datatables.min.js");
            writeScript(page, "/_resource/cms/datepicker/bootstrap-datepicker.min.js");
            writeScript(page, "/_resource/cms/report/reports.js");

            page.writeStart("div", "class", "widget report-widget");
            {
                page.writeStart("h1");
                page.writeHtml("Reports Dashboard");
                page.writeEnd();

                page.writeStart("form", "id", "report-form");
                {
                    page.writeStart("div", "id", "reports");
                    {
                        List<AbstractReport> reports = ReportTool.get(ReportTool::getReports);
                        if (CollectionUtils.isNullOrEmpty(reports)) {
                            page.writeStart("span", "class", "error");
                            page.writeHtml("No reports configured in global settings");
                            page.writeEnd();
                        } else {
                            page.writeStart("h3");
                            page.writeHtml("Reports");
                            page.writeEnd();

                            page.writeStart("select", "id", ACTION_REPORT);
                            {
                                page.writeStart("option", "value", "");
                                page.writeEnd();

                                for (AbstractReport report : reports) {
                                    page.writeStart("option", "value", report.getId());
                                    page.writeHtml(report.getName());
                                    page.writeEnd();
                                }
                            }
                            page.writeEnd();

                            page.writeStart("button", "class", "icon icon-filter", "id", "report-select-button");
                            page.writeHtml("Select");
                            page.writeEnd();
                        }
                    }
                    page.writeEnd();

                    page.writeStart("div", "id", "report-filters");
                    page.writeEnd();
                }
                page.writeEnd();

                page.writeStart("div", "id", "report-table");
                page.writeEnd();
            }
            page.writeEnd();
        }
    }

    public static void writeFilter(ToolPageContext page, ReportFilter filter, Query<?> query) throws IOException {
        if (filter instanceof TypedFilter) {
            TypedFilter typedFilter = (TypedFilter) filter;

            // render checkbox filter
            if (typedFilter.getType() instanceof CheckboxFilterType) {
                CheckboxFilterType filterType = (CheckboxFilterType) typedFilter.getType();
                if (!StringUtils.isBlank(typedFilter.getHeading())) {
                    writeTitle(page,
                            typedFilter.getHeading(),
                            typedFilter.isOnNewLine()
                    );

                    writeCheckboxControl(page,
                            typedFilter.getName(),
                            typedFilter.getLabel(),
                            filterType.getValue(),
                            typedFilter.getExtraClass(),
                            filterType.isCheckedByDefault(),
                            false
                    );
                } else {
                    writeCheckboxControl(page,
                            typedFilter.getName(),
                            typedFilter.getLabel(),
                            filterType.getValue(),
                            typedFilter.getExtraClass(),
                            filterType.isCheckedByDefault(),
                            filter.isOnNewLine()
                    );
                }
            }

            // render date range filter
            if (typedFilter.getType() instanceof DateRangeFilterType) {
                DateRangeFilterType filterType = (DateRangeFilterType) typedFilter.getType();

                writeDateRangeControl(page,
                        filter.getName(),
                        filter.getLabel(),
                        filter.getExtraClass(),
                        filterType.isCurrentMonthByDefault(),
                        filterType.isRequired(),
                        filterType.isRecalcAvailable(),
                        filter.isOnNewLine());
            }

            // render dropdown filter
            if (typedFilter.getType() instanceof DropdownFilterType) {
                DropdownFilterType filterType = (DropdownFilterType) ((TypedFilter) filter).getType();

                writeSelectControl(page,
                        filterType.getOptions(),
                        filter.getName(),
                        filter.getLabel(),
                        filter.getExtraClass(),
                        false,
                        filter.isOnNewLine());
            }
        }

        // auto-generate filter dropdown options from indexed field values
        if (filter instanceof GenericFilter) {
            if (AbstractReport.FILTER_BY_SITE.equalsIgnoreCase(filter.getName())) {
                // render market/site selection (special case)
                List<Site> sites = Site.Static.findAll();
                writeSelectControl(page,
                        sites,
                        filter.getName(),
                        filter.getLabel(),
                        filter.getExtraClass(),
                        true,
                        filter.isOnNewLine()
                );
                return;
            }

            ObjectType type = ObjectType.getInstance(query.getObjectClass());
            ObjectField field = type.getField(filter.getName());

            if (field == null) {
                LOGGER.error("Failed to find field name '{}' on object type '{}'", filter.getName(), type.getDisplayName());
                return;
            }

            // render enum options (special case)
            if (!ObjectUtils.isBlank(field.getJavaEnumClassName())) {
                writeSelectControl(page,
                        field.getValues(),
                        filter.getName(),
                        filter.getLabel(),
                        filter.getExtraClass(),
                        true,
                        filter.isOnNewLine());
            } else {
                switch (field.getInternalType()) {
                    case "date":
                        writeDateRangeControl(page,
                                filter.getName(),
                                filter.getLabel(),
                                filter.getExtraClass(),
                                true,
                                false,
                                false,
                                filter.isOnNewLine());
                        break;
                    case "record":
                    case "set/record":
                    case "list/record":
                    case "text":
                    case "set/text":
                    case "list/text":
                    case "number":
                        List<Object> options = new ArrayList<>();
                        if (!StringUtils.isBlank(filter.getDefaultValue())) {
                            options.add(filter.getDefaultValue());
                        }
                        options.addAll(getAllValuesForField(query, filter.getName()));

                        sort(options);

                        writeSelectControl(page,
                                options,
                                filter.getName(),
                                filter.getLabel(),
                                filter.getExtraClass(),
                                true,
                                filter.isOnNewLine());
                        break;
                    default:
                        LOGGER.error("Type '{}' is not handled", field.getInternalType());
                        break;
                }
            }
        }
    }

    /**
     * Queries all possible values for a given field in a query result set
     * Ensure the object is either a String or a Record which has a label
     */
    public static Set<Object> getAllValuesForField(Query<?> query, String fieldName) {
        Set<Object> results = new HashSet<>();

        List<Grouping<?>> groupings = Utils.uncheckedCast(query.groupBy(fieldName));
        for (Grouping<?> grouping : groupings) {
            if (!ObjectUtils.isBlank(grouping.getKeys())) {
                Object key = grouping.getKeys().get(0);
                if ((key instanceof Record && ((Record) key).getLabel() != null) || key instanceof Number) {
                    results.add(key);
                } else if (key instanceof String) {
                    results.add(WordUtils.capitalize((String) key));
                }
            }
        }
        return results;
    }

    public static void sort(List<Object> options) {
        options.sort((Object o1, Object o2) -> {
            String s1 = o1 instanceof Record ? ((Record) o1).getLabel() : String.valueOf(o1);
            String s2 = o2 instanceof Record ? ((Record) o2).getLabel() : String.valueOf(o2);
            return s1.compareToIgnoreCase(s2);
        });
    }

    /**
     * Writes a <input type="checkbox"></input> html element
     */
    private static void writeCheckboxControl(ToolPageContext page, String name, String label, String value, String extraClass, Boolean checked, Boolean newLine) throws IOException {
        if (newLine) {
            page.write("<br/>");
        }

        page.writeStart("div", "class", String.join(" ", Arrays.asList("checkbox-filter-control", extraClass)));
        {
            page.writeStart("label", "for", name);
            page.write(label + ": ");
            page.writeEnd();

            if (checked) {
                page.writeStart("input", "name", name, "type", "checkbox", "value", value, "style", "margin-top: 4px", "checked", "checked");
            } else {
                page.writeStart("input", "name", name, "type", "checkbox", "value", value, "style", "margin-top: 4px");
            }
            page.writeEnd();
        }
        page.writeEnd();
    }

    /**
     * Writes a <select></select> html element
     */
    private static void writeSelectControl(ToolPageContext page, Collection<?> options, String name, String label, String extraClass, Boolean includeAllOption, Boolean newLine) throws IOException {
        if (newLine) {
            page.write("<br/>");
        }

        page.writeStart("div", "class", String.join(" ", Arrays.asList("filter-control", extraClass)));
        {
            page.writeStart("label", "for", name);
            page.write(label + ": ");
            page.writeEnd();

            page.writeStart("select", "name", name);
            {
                if (includeAllOption) {
                    page.writeStart("option", "value", "");
                    page.write("All");
                    page.writeEnd();
                }

                if (!options.isEmpty()) {
                    for (Object option : options) {
                        if (option instanceof ObjectField.Value) {
                            ObjectField.Value enumEntry = (ObjectField.Value) option;
                            page.writeStart("option", "value", enumEntry.getValue());
                            page.write(enumEntry.getLabel());
                            page.writeEnd();
                            continue;
                        }

                        if (option instanceof DropdownOption) {
                            DropdownOption dropdownOption = (DropdownOption) option;

                            if (dropdownOption.isSelected()) {
                                page.writeStart("option", "value", dropdownOption.getValue(), "selected", true);
                            } else {
                                page.writeStart("option", "value", dropdownOption.getValue());
                            }
                            page.write(dropdownOption.getLabel());
                            page.writeEnd();
                            continue;
                        }

                        if (option instanceof Record) {
                            Record record = (Record) option;
                            if (record.getId() != null && record.getLabel() != null) {
                                page.writeStart("option", "value", record.getId().toString());
                                page.write(record.getLabel());
                                page.writeEnd();
                            }
                            continue;
                        }

                        // output the string version as value & label as default
                        page.writeStart("option", "value", option.toString());
                        page.write(option.toString());
                        page.writeEnd();
                    }
                }
            }
            page.writeEnd();
        }
        page.writeEnd();
    }

    /**
     * Writes 2 <input type="text" class="date"></input> html elements which will be
     * converted to a date widget later
     */
    private static void writeDateRangeControl(ToolPageContext page, String name, String label, String extraClass, Boolean currentMonth, Boolean required, Boolean addRecalc, Boolean newLine) throws IOException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");

        if (newLine) {
            page.write("<br/>");
        }

        page.writeStart("div", "class", String.join(" ", Arrays.asList("filter-control", extraClass)));
        {
            page.writeStart("label", "for", (name + "_from"));
            page.write(label + ": ");
            page.writeEnd();

            page.writeHtml("From ");
            page.writeTag("input",
                    "type", "text",
                    "class", required ? "date required from_date" : "date from_date",
                    "name", name + "_from",
                    "value", currentMonth ? formatter.print(new DateTime().dayOfMonth().withMinimumValue()) : "",
                    "placeholder", "Any Time");

            page.writeHtml(" To ");
            page.writeTag("input",
                    "type", "text",
                    "class", "date to_date",
                    "name", name + "_to",
                    "value", currentMonth ? formatter.print(new DateTime().dayOfMonth().withMaximumValue()) : "",
                    "placeholder", "Any Time");

            if (addRecalc) {
                page.writeStart("button", "id", "update-filters-button", "class", "icon icon-filter");
                page.write("Update Filters");
                page.writeEnd();
            }
        }
        page.writeEnd();
    }

    private static void writeTitle(ToolPageContext page, String text, Boolean newLine) throws IOException {
        if (newLine) {
            page.write("<br/><br/>");
        }

        if (!StringUtils.isBlank(text)) {
            page.writeStart("h3");
            page.write(text);
            page.writeEnd();
        }
    }

    private static void writeScript(ToolPageContext page, String filePath) throws IOException {
        page.writeStart("script", "src", filePath);
        page.writeEnd();
    }

    private static void writeStylesheetLink(ToolPageContext page, String filePath) throws IOException {
        page.writeStart("link", "rel", "stylesheet", "href", filePath);
        page.writeEnd();
    }
}
