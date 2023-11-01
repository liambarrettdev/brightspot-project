package com.brightspot.model.form.field.captcha;

import com.brightspot.integration.IntegrationSiteSettings;
import com.brightspot.model.form.field.Field;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;

@ToolUi.NoteHtml("<span data-dynamic-html='${content.getNoteHtml()}'></span>")
@ViewBinding(value = CaptchaFieldViewModel.class, types = CaptchaField.VIEW_TYPE)
public class CaptchaField extends Field {

    protected static final String VIEW_TYPE = "captcha-field";

    // --- Overrides --- //

    @Override
    public String getViewClass() {
        return VIEW_TYPE;
    }

    // --- Helper Methods --- //

    public String getNoteHtml() {
        Site site = Utils.getCurrentSite();
        if (site == null) {
            return LocalizationUtils.currentSiteText(this, "message.nosite");
        } else if (IntegrationSiteSettings.get(site, IntegrationSiteSettings::getCaptchaProvider) == null) {
            return LocalizationUtils.currentSiteText(this, "message.noprovider");
        }

        return null;
    }
}
