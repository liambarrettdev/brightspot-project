package com.brightspot.integration.google.captcha;

import com.psddev.cms.view.ViewModel;

public class GoogleCaptchaViewModel extends ViewModel<GoogleCaptchaProvider> {

    public Object getKey() {
        return model.getClientKey();
    }
}
