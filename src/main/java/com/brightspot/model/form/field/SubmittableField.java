package com.brightspot.model.form.field;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.psddev.dari.db.Recordable;

public interface SubmittableField extends Recordable {

    Map<String, String> getSubmittedValue(HttpServletRequest request);
}
