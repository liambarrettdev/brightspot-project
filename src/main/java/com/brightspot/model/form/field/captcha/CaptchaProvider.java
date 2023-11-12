package com.brightspot.model.form.field.captcha;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.brightspot.tool.Wrapper;

public interface CaptchaProvider extends Wrapper {

    List<String> validate(HttpServletRequest request);
}
