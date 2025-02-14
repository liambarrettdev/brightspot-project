package com.brightspot.tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import com.brightspot.model.error.handler.ErrorHandler;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.page.Page;
import com.brightspot.model.page.footer.Footer;
import com.brightspot.model.page.header.Header;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.BeanProperty("site")
@Recordable.FieldInternalNamePrefix("settings.site.")
public class CustomSiteSettings extends Modification<Site> {

    public static final String TAB_ADVANCED = "Advanced";
    public static final String TAB_LAYOUT = "Layout";

    @ToolUi.Placeholder(dynamicText = "${content.site.getSiteEmailFallback()}")
    private String siteEmail;

    private Locale locale;

    private Page homepage;

    @ToolUi.Heading("Error Handling")
    @ToolUi.Tab(TAB_ADVANCED)
    private Set<ErrorHandler> errorHandlers;

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

    public String getSiteEmail() {
        return ObjectUtils.firstNonBlank(siteEmail, getSiteEmailFallback());
    }

    public void setSiteEmail(String siteEmail) {
        this.siteEmail = siteEmail;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Page getHomepage() {
        return homepage;
    }

    public void setHomepage(Page homepage) {
        this.homepage = homepage;
    }

    public Set<ErrorHandler> getErrorHandlers() {
        if (errorHandlers == null) {
            errorHandlers = new HashSet<>();
        }
        return errorHandlers;
    }

    public void setErrorHandlers(Set<ErrorHandler> errorHandlers) {
        this.errorHandlers = errorHandlers;
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

    // -- Helper Methods -- //

    public String getSiteEmailFallback() {
        return CustomGlobalSettings.get(CustomGlobalSettings::getDefaultEmail);
    }

    // -- Static Methods --//

    public static <T> T get(Site site, Function<CustomSiteSettings, T> getter) {
        return site == null
            ? null
            : getter.apply(site.as(CustomSiteSettings.class));
    }
}
