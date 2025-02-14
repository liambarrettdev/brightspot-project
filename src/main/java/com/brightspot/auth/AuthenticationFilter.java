package com.brightspot.auth;

import java.util.List;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.servlet.LoginServlet;
import com.brightspot.servlet.LogoutServlet;
import com.brightspot.tool.auth.AuthenticationSiteSettings;
import com.psddev.cms.db.PageFilter;
import com.psddev.dari.util.AbstractFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationFilter extends AbstractFilter implements AbstractFilter.Auto {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final String AUTHENTICATED_SESSION_ATTRIBUTE = "auth.session";
    private static final String AUTHENTICATED_ENTITY_ATTRIBUTE = "auth.entity";

    // -- Overrides -- //

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        if (isAllAccessAllowed(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        Session session = getSession(request);
        if (session != null) {
            response.setHeader("Cache-Control", "private, no-cache");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void updateDependencies(Class<? extends AbstractFilter> filterClass, List<Class<? extends Filter>> dependencies) {
        if (PageFilter.class.isAssignableFrom(filterClass)) {
            dependencies.add(getClass());
        }
    }

    // -- Utility Methods -- //

    private boolean isAllAccessAllowed(String requestUri) {
        return requestUri.startsWith("/_")
            || requestUri.startsWith("/cms")
            || requestUri.startsWith(LoginServlet.SERVLET_PATH)
            || requestUri.startsWith(LogoutServlet.SERVLET_PATH);
    }

    // -- Static Methods -- //

    public static AuthenticationSettings getAuthenticator(HttpServletRequest request) {
        return Optional.ofNullable(PageFilter.Static.getSite(request))
            .map(s -> AuthenticationSiteSettings.get(s, AuthenticationSiteSettings::getSettings))
            .orElse(null);
    }

    public static Session getAuthenticatedSession(HttpServletRequest request) {
        try {
            return getSession(request);
        } catch (Exception e) {
            LOGGER.warn("Unable to get authenticated session: {}", e.getMessage());
            return null;
        }
    }

    public static AuthenticationUser getAuthenticatedUser(HttpServletRequest request) {
        return Optional.ofNullable(getAuthenticatedSession(request))
            .map(Session::getUser)
            .orElse(null);
    }

    private static Session getSession(HttpServletRequest request) throws Exception {
        Session session = (Session) request.getAttribute(AUTHENTICATED_SESSION_ATTRIBUTE);

        if (session == null) {
            AbstractAuthenticator authenticator = Optional.ofNullable(getAuthenticator(request))
                .map(AuthenticationSettings::getAuthenticator)
                .orElse(null);

            if (authenticator != null) {
                session = authenticator.getCurrentSession(request);
                if (session != null) {
                    request.setAttribute(AUTHENTICATED_SESSION_ATTRIBUTE, session);
                    request.setAttribute(AUTHENTICATED_ENTITY_ATTRIBUTE, session.getUser());
                }
            }
        }

        return session;
    }
}
