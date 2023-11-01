package com.brightspot.model.form.field.captcha;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.psddev.dari.db.Recordable;

public interface CaptchaProvider extends Recordable {

    List<String> validate(HttpServletRequest request);
}
