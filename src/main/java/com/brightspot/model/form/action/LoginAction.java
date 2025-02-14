package com.brightspot.model.form.action;

import com.brightspot.model.form.FormModule;
import com.brightspot.servlet.LoginServlet;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ToolUi.Note("Requires that the form must have 'email' and 'password' fields")
public class LoginAction extends Record implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAction.class);

    // -- Overrides -- //

    @Override
    public boolean onSubmit(FormModule form) {
        return true;
    }

    @Override
    public String getRedirectTarget() {
        return LoginServlet.SERVLET_PATH;
    }
}
