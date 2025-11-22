package com.brightspot.servlet;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.auth.AbstractAuthenticator;
import com.brightspot.auth.AuthenticationException;
import com.brightspot.auth.AuthenticationFilter;
import com.brightspot.auth.AuthenticationSettings;
import com.brightspot.utils.DirectoryUtils;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.dari.util.JspUtils;
import com.psddev.dari.util.RoutingFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MultipartConfig
@RoutingFilter.Path(LogoutServlet.SERVLET_PATH)
public class LogoutServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutServlet.class);

    public static final String SERVLET_PATH = "/_auth/logout";

    // -- Overrides -- //

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Site site = PageFilter.Static.getSite(request);

        AuthenticationSettings settings = AuthenticationFilter.getAuthenticationSettings(request);
        AbstractAuthenticator authenticator = Optional.ofNullable(settings)
            .map(AuthenticationSettings::getAuthenticator)
            .orElse(null);

        if (authenticator == null) {
            throw new AuthenticationException("No Authenticator configured for url: " + request.getRequestURL());
        }

        authenticator.logout(request, response);

        // redirect back to Referer on logout
        String redirect = DirectoryUtils.getCanonicalUrl(site, settings.getUnauthenticatedLandingPage());
        if (StringUtils.isBlank(redirect)) {
            redirect = request.getHeader("Referer");
        }

        JspUtils.redirect(request, response, redirect);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug("rerouting request as GET");
        super.doGet(request, response);
    }
}
