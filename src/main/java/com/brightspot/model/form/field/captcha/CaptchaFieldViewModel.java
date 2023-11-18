package com.brightspot.model.form.field.captcha;

import java.util.Collection;
import java.util.Collections;

import com.brightspot.integration.IntegrationSiteSettings;
import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.form.input.CaptchaInputView;

public class CaptchaFieldViewModel extends AbstractViewModel<CaptchaField> implements CaptchaInputView {

    @Override
    protected boolean shouldCreate() {
        return IntegrationSiteSettings.get(getCurrentSite(), IntegrationSiteSettings::getCaptchaProvider) != null;
    }

    @Override
    public Object getLabel() {
        return model.getLabel();
    }

    @Override
    public Collection<?> getCaptcha() {
        CaptchaProvider provider = IntegrationSiteSettings.get(getCurrentSite(), IntegrationSiteSettings::getCaptchaProvider);

        return Collections.singleton(buildObjectView(provider));
    }
}
