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

/**
 * Servlet filter that enforces authentication on requests.
 *
 * <p>This filter runs before {@link PageFilter} and checks for authenticated sessions on
 * incoming requests. It caches sessions in request attributes for performance and sets
 * cache control headers for authenticated requests.</p>
 *
 * <p>The filter automatically excludes certain paths from authentication requirements:</p>
 * <ul>
 *   <li>Paths starting with {@code /_} (internal API paths)</li>
 *   <li>Paths starting with {@code /cms} (CMS admin paths)</li>
 *   <li>Login and logout endpoints</li>
 * </ul>
 *
 * <p>Sessions are retrieved from the authenticator configured in site settings and cached
 * in request attributes for subsequent use during the request lifecycle.</p>
 *
 * @see AbstractFilter
 * @see PageFilter
 * @see AuthenticationSettings
 */
public class AuthenticationFilter extends AbstractFilter implements AbstractFilter.Auto {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final String AUTHENTICATED_SESSION_ATTRIBUTE = "auth.session";
    private static final String AUTHENTICATED_ENTITY_ATTRIBUTE = "auth.entity";

    // -- Overrides -- //

    /**
     * Processes the request and checks for authentication.
     *
     * <p>If authentication is not required for the request URI, the request is passed through.
     * Otherwise, the filter attempts to retrieve an authenticated session and sets cache
     * control headers if a session exists.</p>
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param chain the filter chain
     * @throws Exception if an error occurs during filtering
     */
    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        if (noAuthenticationRequired(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        Session session = cacheSession(request);
        if (session != null) {
            response.setHeader("Cache-Control", "private, no-cache");
        }

        chain.doFilter(request, response);
    }

    /**
     * Ensures this filter runs before PageFilter.
     *
     * @param filterClass the filter class being processed
     * @param dependencies the list of filter dependencies to update
     */
    @Override
    public void updateDependencies(Class<? extends AbstractFilter> filterClass, List<Class<? extends Filter>> dependencies) {
        if (PageFilter.class.isAssignableFrom(filterClass)) {
            dependencies.add(getClass());
        }
    }

    // -- Static Methods -- //

    /**
     * Gets the authentication settings for the current site.
     *
     * <p>Retrieves the site from the request and returns its authentication settings,
     * or {@code null} if no site is available or no settings are configured.</p>
     *
     * @param request the HTTP servlet request
     * @return the authentication settings for the current site, or {@code null}
     */
    public static AuthenticationSettings getAuthenticationSettings(HttpServletRequest request) {
        return Optional.ofNullable(PageFilter.Static.getSite(request))
            .map(s -> AuthenticationSiteSettings.get(s, AuthenticationSiteSettings::getSettings))
            .orElse(null);
    }

    /**
     * Gets the authenticated session from the request with error handling.
     *
     * <p>This method wraps {@link #cacheSession(HttpServletRequest)} and catches any exceptions,
     * logging warnings instead of throwing errors. This allows the application to continue
     * even if session retrieval fails.</p>
     *
     * @param request the HTTP servlet request
     * @return the authenticated session, or {@code null} if no session exists or an error occurs
     */
    public static Session getAuthenticatedSession(HttpServletRequest request) {
        try {
            return cacheSession(request);
        } catch (Exception e) {
            LOGGER.warn("Unable to get authenticated session: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Gets the authenticated user from the request.
     *
     * <p>Retrieves the authenticated session and returns the associated user, or {@code null}
     * if no session exists.</p>
     *
     * @param request the HTTP servlet request
     * @return the authenticated user, or {@code null} if no session exists
     */
    public static AuthenticationUser getAuthenticatedUser(HttpServletRequest request) {
        return Optional.ofNullable(getAuthenticatedSession(request))
            .map(Session::getUser)
            .orElse(null);
    }

    /**
     * Checks if authentication is not required for the given request URI.
     *
     * <p>Authentication is not required for:</p>
     * <ul>
     *   <li>Paths starting with {@code /_} (internal API paths)</li>
     *   <li>Paths starting with {@code /cms} (CMS admin paths)</li>
     *   <li>Login endpoint ({@link LoginServlet#SERVLET_PATH})</li>
     *   <li>Logout endpoint ({@link LogoutServlet#SERVLET_PATH})</li>
     * </ul>
     *
     * @param requestUri the request URI to check
     * @return {@code true} if authentication is not required, {@code false} otherwise
     */
    public static boolean noAuthenticationRequired(String requestUri) {
        return requestUri.startsWith("/_")
            || requestUri.startsWith("/cms")
            || requestUri.startsWith(LoginServlet.SERVLET_PATH)
            || requestUri.startsWith(LogoutServlet.SERVLET_PATH);
    }

    /**
     * Gets the authenticated session from the request.
     *
     * <p>First checks for a cached session in request attributes. If not found, retrieves
     * the authenticator from site settings and uses it to get the current session. The
     * session is then cached in request attributes for subsequent use.</p>
     *
     * @param request the HTTP servlet request
     * @return the authenticated session, or {@code null} if no session exists
     * @throws Exception if an error occurs while retrieving the session
     */
    private static Session cacheSession(HttpServletRequest request) throws Exception {
        // get cached session
        Session session = (Session) request.getAttribute(AUTHENTICATED_SESSION_ATTRIBUTE);

        if (session == null) {
            // check for authentication cookie
            AbstractAuthenticator authenticator = Optional.ofNullable(getAuthenticationSettings(request))
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
