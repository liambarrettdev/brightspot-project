package com.brightspot.auth;

import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.model.user.PasswordUser;
import com.brightspot.model.user.User;
import com.brightspot.utils.CookieUtils;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.JspUtils;
import org.apache.commons.lang3.StringUtils;

public class PasswordAuthenticator extends AbstractAuthenticator {

    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_PASSWORD = "password";

    @Required
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
    public Session getCurrentSession(HttpServletRequest request) throws AuthenticationException {
        return Optional.ofNullable(JspUtils.getSignedCookie(request, COOKIE_NAME))
            .filter(StringUtils::isNotBlank)
            .map(Session::findSession)
            .orElse(null);
    }

    @Override
    public Session login(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuthenticationUser user = authenticate(request);
        if (user == null) {
            throw new AuthenticationException("Could not authenticate user");
        }

        return addUserSession(request, response, user);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        CookieUtils.setCookie(request, response, COOKIE_NAME, "", 0);

        Session session = Optional.ofNullable(getCurrentSession(request))
            .orElseThrow(() -> new AuthenticationException("No authenticated session for logout request"));

        session.setEnd(new Date());
        session.saveImmediately();
    }

    // -- Utility Methods -- //

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
