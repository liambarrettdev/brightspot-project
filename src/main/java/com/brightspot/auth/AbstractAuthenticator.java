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
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.JspUtils;

/**
 * Base class for implementing custom authentication methods.
 *
 * <p>This abstract class provides the foundation for authentication functionality and defines
 * the contract that all authenticators must implement. It handles session creation, cookie
 * management, and authentication token headers.</p>
 *
 * <p>Subclasses must implement the abstract methods to provide specific authentication logic:</p>
 * <ul>
 *   <li>{@link #getCurrentSession(HttpServletRequest)} - Retrieve current session from request</li>
 *   <li>{@link #login(HttpServletRequest, HttpServletResponse)} - Authenticate user and create session</li>
 *   <li>{@link #logout(HttpServletRequest, HttpServletResponse)} - Terminate user session</li>
 * </ul>
 *
 * <p>All authenticators must configure email templates for user registration with the
 * {@code ${link}} placeholder for verification links.</p>
 *
 * @see PasswordAuthenticator
 */
@ToolUi.Publishable(false)
public abstract class AbstractAuthenticator extends Record {

    public static final String COOKIE_NAME = "bsp.auth";
    public static final String KEY_LINK = "${link}";

    protected static final String AUTH_TOKEN_NAME = "auth_token";
    protected static final String AUTH_HEADER_NAME = "Native-Authorization";
    protected static final String AUTH_BEARER_TYPE = "Bearer";
    protected static final Integer DEFAULT_MAX_AGE = 86400;

    @ToolUi.Heading("Registration Email")

    @Recordable.Required
    private String from;

    @Recordable.Required
    private String subject;

    @Recordable.Required
    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String template;

    /**
     * Retrieves the current authenticated session from the request.
     *
     * <p>This method should extract the session identifier from the request (typically from
     * a cookie or header) and return the corresponding session if it exists and is valid.</p>
     *
     * @param request the HTTP servlet request
     * @return the current session, or {@code null} if no valid session exists
     * @throws AuthenticationException if an error occurs while retrieving the session
     */
    public abstract Session getCurrentSession(HttpServletRequest request) throws AuthenticationException;

    /**
     * Authenticates a user and creates a new session.
     *
     * <p>This method should extract credentials from the request, validate them, and create
     * a new session for the authenticated user. The session should be stored and authentication
     * cookies/headers should be set in the response.</p>
     *
     * @param request the HTTP servlet request containing authentication credentials
     * @param response the HTTP servlet response to set authentication cookies/headers
     * @return the newly created session for the authenticated user
     * @throws AuthenticationException if authentication fails or credentials are invalid
     */
    public abstract Session login(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;

    /**
     * Terminates the current user session.
     *
     * <p>This method should invalidate the current session and clear any authentication
     * cookies or headers from the response.</p>
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response to clear authentication cookies/headers
     * @throws AuthenticationException if no session exists or an error occurs during logout
     */
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

    /**
     * Creates a new session for a user and sets authentication cookies and headers.
     *
     * <p>This method creates a new session, stores it in the database, sets a signed
     * authentication cookie, and adds an authentication token to the response header.
     * The cookie has a default max age of 24 hours.</p>
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response to set cookies and headers
     * @param user the authenticated user to create a session for
     * @return the newly created session, or {@code null} if the user is null
     */
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
            .map(e -> new String(Base64.getEncoder().encode(e.getBytes(StandardCharsets.UTF_8))))
            .ifPresent(e -> setAuthToken(response, e));
    }

    private static void setAuthToken(HttpServletResponse response, String token) {
        response.setHeader(AUTH_HEADER_NAME, AUTH_BEARER_TYPE + " " + token);
    }
}
