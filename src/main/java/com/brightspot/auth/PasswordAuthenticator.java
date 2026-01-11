package com.brightspot.auth;

import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.model.user.PasswordUser;
import com.brightspot.model.user.User;
import com.brightspot.tool.SiteSingleton;
import com.brightspot.utils.CookieUtils;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.JspUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Password-based authentication implementation.
 *
 * <p>This authenticator provides email/password-based authentication. It validates user
 * credentials by checking the email and password against the database, verifies user status
 * (must be VERIFIED, not PENDING or BLOCKED), and creates sessions for authenticated users.</p>
 *
 * <p>Users are queried site-scoped if a site is available in the request context, otherwise
 * only users without a site owner are considered.</p>
 *
 * <p>Request parameters:</p>
 * <ul>
 *   <li>{@value #PARAM_EMAIL} - User email address</li>
 *   <li>{@value #PARAM_PASSWORD} - User password</li>
 * </ul>
 *
 * @see AbstractAuthenticator
 * @see User
 * @see PasswordUser
 */
public class PasswordAuthenticator extends AbstractAuthenticator implements SiteSingleton {

    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_PASSWORD = "password";

    @Recordable.Required
    @ToolUi.DisplayFirst
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // -- Overrides -- //

    @Override
    protected void afterCreate() {
        setName("Default");
    }

    @Override
    public String getLabel() {
        return "Password";
    }

    /**
     * Retrieves the current session from the authentication cookie.
     *
     * <p>Extracts the session ID from the signed cookie and looks up the session in the database.
     * Only returns sessions that are not expired.</p>
     *
     * @param request the HTTP servlet request
     * @return the current session, or {@code null} if no valid session exists
     * @throws AuthenticationException if an error occurs while retrieving the session
     */
    @Override
    public Session getCurrentSession(HttpServletRequest request) throws AuthenticationException {
        return Optional.ofNullable(JspUtils.getSignedCookie(request, COOKIE_NAME))
            .filter(StringUtils::isNotBlank)
            .map(Session::findSession)
            .orElse(null);
    }

    /**
     * Authenticates a user using email and password credentials.
     *
     * <p>Validates the email and password, checks user status, and creates a session
     * for authenticated users. Throws {@link AuthenticationException} for various failure
     * scenarios including invalid credentials, pending verification, or blocked users.</p>
     *
     * @param request the HTTP servlet request containing email and password parameters
     * @param response the HTTP servlet response to set authentication cookies/headers
     * @return the newly created session for the authenticated user
     * @throws AuthenticationException if authentication fails, user not found, user is pending
     *         verification, user is blocked, password is missing, or password is invalid
     */
    @Override
    public Session login(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuthenticationUser user = authenticate(request);
        if (user == null) {
            throw new AuthenticationException("Could not authenticate user");
        }

        return addUserSession(request, response, user);
    }

    /**
     * Terminates the current user session.
     *
     * <p>Clears the authentication cookie and sets the session end date to mark it as expired.</p>
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response to clear authentication cookies
     * @throws AuthenticationException if no authenticated session exists for the logout request
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        CookieUtils.setCookie(request, response, COOKIE_NAME, "", 0);

        Session session = Optional.ofNullable(getCurrentSession(request))
            .orElseThrow(() -> new AuthenticationException("No authenticated session for logout request"));

        session.setEnd(new Date());
        session.saveImmediately();
    }

    // -- Utility Methods -- //

    /**
     * Authenticates a user based on email and password from the request.
     *
     * <p>This method performs the following validation steps:</p>
     * <ol>
     *   <li>Extracts email and password from request parameters</li>
     *   <li>Queries for user by email (site-scoped if site is available)</li>
     *   <li>Validates user status (must be VERIFIED, not PENDING or BLOCKED)</li>
     *   <li>Verifies password using {@link PasswordUser.Data}</li>
     * </ol>
     *
     * @param request the HTTP servlet request containing email and password parameters
     * @return the authenticated user, or {@code null} if credentials are missing
     * @throws AuthenticationException if user not found, user is pending verification,
     *         user is blocked, password is missing, or password is invalid
     */
    private AuthenticationUser authenticate(HttpServletRequest request) throws AuthenticationException {
        String emailParam = request.getParameter(PARAM_EMAIL);
        String passwordParam = request.getParameter(PARAM_PASSWORD);

        if (StringUtils.isAnyBlank(emailParam, passwordParam)) {
            return null;
        }

        // find valid user
        Query<User> userQuery = Query.from(User.class).where("email = ?", emailParam);

        Site site = PageFilter.Static.getSite(request);
        if (site != null) {
            userQuery.where(site.itemsPredicate());
        } else {
            userQuery.where("cms.site.owner = missing");
        }

        User user = userQuery.first();
        if (user == null) {
            throw new AuthenticationException("Not a password authenticated user: " + emailParam);
        }

        // check user status
        User.Status status = user.getStatus();

        if (status == null) {
            throw new AuthenticationException("Pending verification");
        }

        if (status == User.Status.BLOCKED) {
            throw new AuthenticationException("Invalid credentials");
        }

        // check user password
        PasswordUser.Data data = user.asPasswordUserData();

        if (data.getPassword() == null) {
            throw new AuthenticationException("Please reset the password for user with email: " + emailParam);
        }

        if (!data.getPassword().check(passwordParam)) {
            throw new AuthenticationException("Invalid credentials");
        }

        return user;
    }
}
