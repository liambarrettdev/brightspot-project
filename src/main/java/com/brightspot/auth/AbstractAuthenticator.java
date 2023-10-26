package com.brightspot.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;

@ToolUi.Publishable(false)
public abstract class AbstractAuthenticator extends Record {

    public abstract Session getCurrentSession(HttpServletRequest request) throws AuthenticationException;

    public abstract Session login(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException;

    public abstract void logout(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException;
}
