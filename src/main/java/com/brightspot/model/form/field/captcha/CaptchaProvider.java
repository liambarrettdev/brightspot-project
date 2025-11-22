package com.brightspot.model.form.field.captcha;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.brightspot.tool.ViewWrapper;

public interface CaptchaProvider extends ViewWrapper {

    List<String> validate(HttpServletRequest request);
}
