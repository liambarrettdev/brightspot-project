package com.brightspot.model.event;

import com.brightspot.model.link.Link;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.DisplayName("Calendar")
@ViewBinding(value = CalendarModuleViewModel.class, types = CalendarModule.VIEW_TYPE)
public class CalendarModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_TYPE = "calendar-module";

    private static final String DEFAULT_MAX_ITEMS = "20";
    private static final String DEFAULT_NO_EVENTS_MESSAGE = "No events found";

    @Recordable.Required
    private String title;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String description;

    private Event.Type eventType;

    @ToolUi.DisplayName("CTA")
    private Link cta;

    @ToolUi.Heading("Display Options")

    @ToolUi.Placeholder(DEFAULT_MAX_ITEMS)
    private Integer maxItemsPerPage;

    @ToolUi.Placeholder(value = DEFAULT_NO_EVENTS_MESSAGE, editable = true)
    private String noEventsMessage;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Event.Type getEventType() {
        return eventType;
    }

    public void setEventType(Event.Type eventType) {
        this.eventType = eventType;
    }

    public Link getCta() {
        return cta;
    }

    public void setCta(Link cta) {
        this.cta = cta;
    }

    public Integer getMaxItemsPerPage() {
        return ObjectUtils.firstNonBlank(maxItemsPerPage, Integer.parseInt(DEFAULT_MAX_ITEMS));
    }

    public void setMaxItemsPerPage(Integer maxItemsPerPage) {
        this.maxItemsPerPage = maxItemsPerPage;
    }

    public String getNoEventsMessage() {
        return ObjectUtils.firstNonBlank(noEventsMessage, DEFAULT_NO_EVENTS_MESSAGE);
    }

    public void setNoEventsMessage(String noEventsMessage) {
        this.noEventsMessage = noEventsMessage;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }
}
