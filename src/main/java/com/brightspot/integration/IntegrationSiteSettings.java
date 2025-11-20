package com.brightspot.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.brightspot.integration.stripe.StripeSettings;
import com.brightspot.model.form.field.captcha.CaptchaProvider;
import com.brightspot.model.page.HeadItem;
import com.brightspot.model.page.element.CustomHeadElements;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

@Recordable.BeanProperty("integration")
@Recordable.FieldInternalNamePrefix("settings.integration.")
public class IntegrationSiteSettings extends Modification<Site> {

    public static final String TAB_INTEGRATIONS = "Integrations";

    @ToolUi.Heading("Page")

    @ToolUi.Tab(TAB_INTEGRATIONS)
    @Recordable.Embedded
    private List<CustomHeadElements> customHeadElements;

    @ToolUi.Tab(TAB_INTEGRATIONS)
    @Recordable.Embedded
    private List<HeadItem> extraHeadItems;

    @ToolUi.Heading("Forms")

    @ToolUi.Tab(TAB_INTEGRATIONS)
    @Recordable.Embedded
    private CaptchaProvider captchaProvider;

    @ToolUi.Heading("Stripe")

    @ToolUi.Tab(TAB_INTEGRATIONS)
    @Recordable.Embedded
    private StripeSettings stripeSettings;

    public List<CustomHeadElements> getCustomHeadElements() {
        if (customHeadElements == null) {
            customHeadElements = new ArrayList<>();
        }
        return customHeadElements;
    }

    public void setCustomHeadElements(List<CustomHeadElements> customHeadElements) {
        this.customHeadElements = customHeadElements;
    }

    public List<HeadItem> getExtraHeadItems() {
        if (extraHeadItems == null) {
            extraHeadItems = new ArrayList<>();
        }
        return extraHeadItems;
    }

    public void setExtraHeadItems(List<HeadItem> extraHeadItems) {
        this.extraHeadItems = extraHeadItems;
    }

    public CaptchaProvider getCaptchaProvider() {
        return captchaProvider;
    }

    public void setCaptchaProvider(CaptchaProvider captchaProvider) {
        this.captchaProvider = captchaProvider;
    }

    public StripeSettings getStripeSettings() {
        return stripeSettings;
    }

    public void setStripeSettings(StripeSettings stripeSettings) {
        this.stripeSettings = stripeSettings;
    }

    // -- Static Methods --//

    public static <T> T get(Site site, Function<IntegrationSiteSettings, T> getter) {
        return site == null
            ? null
            : getter.apply(site.as(IntegrationSiteSettings.class));
    }
}
