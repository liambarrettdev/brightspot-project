package com.brightspot.model.page.footer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.brightspot.model.container.ColumnContainerModule;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import org.apache.commons.lang3.StringUtils;

@ViewBinding(value = FooterViewModel.class)
public class Footer extends Record {

    private static final String YEAR_TOKEN = "$YYYY";

    @Recordable.Required
    private String name;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String content;

    private ColumnContainerModule links;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    @ToolUi.Note("If published in this text, the special '" + YEAR_TOKEN
        + "' date token will be replaced with the value for the current year.")
    private String copyright;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ColumnContainerModule getLinks() {
        return links;
    }

    public void setLinks(ColumnContainerModule links) {
        this.links = links;
    }

    public String getCopyright() {
        return replaceDateToken(copyright);
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    // -- Utility Methods -- //

    private String replaceDateToken(String text) {
        if (StringUtils.isNotBlank(text) && text.contains(YEAR_TOKEN)) {
            Instant instant = new Date().toInstant();
            LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
            return text.replace(YEAR_TOKEN, DateTimeFormatter.ofPattern("yyyy").format(localDateTime));
        }
        return text;
    }
}
