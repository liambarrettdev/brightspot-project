package com.brightspot.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.brightspot.utils.CookieUtils;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;
import com.psddev.dari.util.JspUtils;

@ToolUi.Publishable(false)
public abstract class AbstractAuthenticator extends Record {

    public static final String COOKIE_NAME = "bsp.auth";
    public static final String KEY_LINK = "${link}";

    protected static final String AUTH_TOKEN_NAME = "auth_token";
    protected static final String AUTH_HEADER_NAME = "Native-Authorization";
    protected static final String AUTH_BEARER_TYPE = "Bearer";
    protected static final Integer DEFAULT_MAX_AGE = 86400;

    @ToolUi.Heading("Registration Email")

    @Required
    private String from;

    @Required
    private String subject;

    @Required
    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String template;

    public abstract Session getCurrentSession(HttpServletRequest request) throws AuthenticationException;

    public abstract Session login(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;

    public abstract void logout(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    // -- Statics -- //

    public static Session addUserSession(HttpServletRequest request, HttpServletResponse response, AuthenticationUser user) {
        Session session = Session.createSession(user);
        if (session == null) {
            return null;
        }

        CookieUtils.setSignedCookie(request, response, COOKIE_NAME, session.getId().toString(), DEFAULT_MAX_AGE);
        AbstractAuthenticator.setAuthToken(response, session);

        return session;
    }

    private static void setAuthToken(HttpServletResponse response, Session session) {
        Optional.ofNullable(session)
            .map(e -> JspUtils.signCookie(AUTH_TOKEN_NAME, e.getId().toString()))
            .map(e -> new String(Base64.getEncoder().encode(e.getBytes()), StandardCharsets.UTF_8))
            .ifPresent(e -> setAuthToken(response, e));
    }

    private static void setAuthToken(HttpServletResponse response, String token) {
        response.setHeader(AUTH_HEADER_NAME, AUTH_BEARER_TYPE + " " + token);
    }
}
