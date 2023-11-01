package com.brightspot.model.form.field.captcha;

import com.brightspot.model.form.field.Field;
import com.psddev.cms.view.ViewBinding;

@ViewBinding(value = CaptchaFieldViewModel.class, types = CaptchaField.VIEW_TYPE)
public class CaptchaField extends Field {

    protected static final String VIEW_TYPE = "captcha-field";

    @Override
    public String getViewClass() {
        return VIEW_TYPE;
    }
}
