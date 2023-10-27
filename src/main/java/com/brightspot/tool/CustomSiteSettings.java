package com.brightspot.tool;

import java.util.Locale;
import java.util.function.Function;

import com.brightspot.model.page.Footer;
import com.brightspot.model.page.Header;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

@Recordable.BeanProperty("site")
@Recordable.FieldInternalNamePrefix("settings.site.")
public class CustomSiteSettings extends Modification<Site> {

    public static final String TAB_LAYOUT = "Layout";

    private Locale locale;

    @ToolUi.Tab(TAB_LAYOUT)
    private Header header;

    @ToolUi.Tab(TAB_LAYOUT)
    private Footer footer;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Footer getFooter() {
        return footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }
    // -- Static Methods --//

    public static <T> T get(Site site, Function<CustomSiteSettings, T> getter) {
        return site == null
            ? null
            : getter.apply(site.as(CustomSiteSettings.class));
    }
}
