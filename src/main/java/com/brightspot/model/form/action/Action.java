package com.brightspot.model.form.action;

import com.brightspot.model.form.FormModule;
import com.psddev.dari.db.Recordable;

public interface Action extends Recordable {

    String onSubmit(FormModule form);
}
