package com.brightspot.model.event;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.promo.PromotableDelegateViewModel;
import com.brightspot.utils.Utils;
import com.brightspot.view.base.util.LinkView;
import com.brightspot.view.model.event.CalendarModuleView;
import com.psddev.cms.view.ViewResponse;
import com.psddev.cms.view.servlet.HttpParameter;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarModuleViewModel extends AbstractViewModel<CalendarModule> implements CalendarModuleView {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarModuleViewModel.class);

    private static final String MONTH_PARAM = "month";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    @HttpParameter(MONTH_PARAM)
    protected String monthParam;

    private YearMonth selectedMonth;

    private transient List<Event> events;

    @Override
    protected void onCreate(ViewResponse response) {
        super.onCreate(response);

        if (StringUtils.isNotBlank(monthParam)) {
            try {
                selectedMonth = YearMonth.parse(monthParam);
            } catch (DateTimeParseException e) {
                LOGGER.error("Invalid month parameter format: {}", monthParam, e);
            }
        }

        if (ObjectUtils.isBlank(selectedMonth)) {
            selectedMonth = YearMonth.now();
        }

        Date startDate = fromLocaDate(selectedMonth.atDay(1));
        Date endDate = fromLocaDate(selectedMonth.plusMonths(1).atDay(1));

        Query<Event> query = getBaseQuery().and("((endDate > ? and endDate < ?) or (endDate > ? and startDate < ?) or (endDate > ? and startDate < ?) or (startDate > ? and startDate < ?))",
            startDate, endDate, endDate, endDate, startDate, startDate, startDate, endDate);

        events = query.sortAscending("startDate")
            .timeout(300D)
            .select(0, model.getMaxItemsPerPage())
            .getItems();
    }

    @Override
    public Object getTitle() {
        return model.getTitle();
    }

    @Override
    public Object getDescription() {
        return buildRichTextView(model.getDescription());
    }

    @Override
    public Object getCurrentMonth() {
        return Optional.ofNullable(selectedMonth)
            .map(FORMATTER::format)
            .orElse(null);
    }

    @Override
    public Object getPrevMonth() {
        YearMonth previousMonth = selectedMonth.minusMonths(1);

        String url = Utils.addQueryParameters("", MONTH_PARAM, previousMonth.toString());
        String text = previousMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());

        return buildLinkView(url, text);
    }

    @Override
    public Object getNextMonth() {
        YearMonth nextMonth = selectedMonth.plusMonths(1);

        String url = Utils.addQueryParameters("", MONTH_PARAM, nextMonth.toString());
        String text = nextMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());

        return buildLinkView(url, text);
    }

    @Override
    public Collection<?> getEvents() {
        return events.stream()
            .map(event -> createView(PromotableDelegateViewModel.class, event))
            .collect(Collectors.toList());
    }

    @Override
    public Object getCta() {
        return createView(LinkView.class, model.getCta());
    }

    @Override
    public Object getDefaultMessage() {
        return events.isEmpty() ? model.getNoEventsMessage() : null;
    }

    private Query<Event> getBaseQuery() {
        Query<Event> query = Query.from(Event.class).where(getCurrentSite().itemsPredicate());
        if (!ObjectUtils.isBlank(model.getEventType())) {
            query.where("type = ?", model.getEventType());
        }
        return query;
    }

    private Date fromLocaDate(LocalDate localDate) {
        return Optional.ofNullable(localDate)
            .map(l -> l.atStartOfDay(ZoneId.systemDefault()))
            .map(ZonedDateTime::toInstant)
            .map(Date::from)
            .orElse(null);
    }
}
