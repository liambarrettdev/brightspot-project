package com.brightspot.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.page.footer.Footer;
import com.brightspot.model.page.header.Header;
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

    @ToolUi.Tab(TAB_LAYOUT)
    private List<AbstractModule> above;

    @ToolUi.Tab(TAB_LAYOUT)
    private List<AbstractModule> aside;

    @ToolUi.Tab(TAB_LAYOUT)
    private List<AbstractModule> below;

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

    public List<AbstractModule> getAbove() {
        if (above == null) {
            above = new ArrayList<>();
        }
        return above;
    }

    public void setAbove(List<AbstractModule> above) {
        this.above = above;
    }

    public List<AbstractModule> getAside() {
        if (aside == null) {
            aside = new ArrayList<>();
        }
        return aside;
    }

    public void setAside(List<AbstractModule> aside) {
        this.aside = aside;
    }

    public List<AbstractModule> getBelow() {
        if (below == null) {
            below = new ArrayList<>();
        }
        return below;
    }

    public void setBelow(List<AbstractModule> below) {
        this.below = below;
    }
    // -- Static Methods --//

    public static <T> T get(Site site, Function<CustomSiteSettings, T> getter) {
        return site == null
            ? null
            : getter.apply(site.as(CustomSiteSettings.class));
    }
}
