package com.brightspot.integration.google.captcha;

import com.brightspot.view.integration.google.GoogleCaptchaView;
import com.psddev.cms.view.ViewModel;

public class GoogleCaptchaViewModel extends ViewModel<GoogleCaptchaProvider> implements GoogleCaptchaView {

    @Override
    public Object getKey() {
        return model.getClientKey();
    }
}
