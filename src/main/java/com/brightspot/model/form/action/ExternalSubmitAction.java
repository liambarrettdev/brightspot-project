package com.brightspot.model.form.action;

import com.brightspot.model.form.FormModule;
import com.brightspot.tool.field.annotation.Url;
import com.psddev.dari.db.Record;

public class ExternalSubmitAction extends Record implements Action {

    @Url
    @Required
    private String endpoint;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String onSubmit(FormModule form) {
        // do something
        return null;
    }
}
