package com.brightspot.tool;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;
import org.apache.commons.lang3.StringUtils;

@Recordable.BeanProperty("site")
@Recordable.FieldInternalNamePrefix("settings.site.")
public class CustomSiteSettings extends Modification<Site> {

    private static final String YEAR_TOKEN = "$YYYY";

    private Locale locale;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    @ToolUi.Note("If published in this text, the special '" + YEAR_TOKEN
        + "' date token will be replaced with the value for the current year.")
    private String copyright;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
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
            Instant instant = (new Date()).toInstant();
            LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
            return text.replace(YEAR_TOKEN, DateTimeFormatter.ofPattern("yyyy").format(localDateTime));
        }
        return text;
    }

    // -- Static Methods --//

    public static <T> T get(Site site, Function<CustomSiteSettings, T> getter) {
        return site == null
            ? null
            : getter.apply(site.as(CustomSiteSettings.class));
    }
}
