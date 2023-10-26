package com.brightspot.servlet;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.auth.AbstractAuthenticator;
import com.brightspot.auth.AuthenticationException;
import com.brightspot.auth.AuthenticationFilter;
import com.brightspot.auth.AuthenticationSettings;
import com.brightspot.auth.Session;
import com.brightspot.utils.DirectoryUtils;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.dari.util.JspUtils;
import com.psddev.dari.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MultipartConfig
@WebServlet(urlPatterns = LoginServlet.PATH)
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);

    public static final String PATH = "/_auth/login";

    // -- Overrides -- //

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        LOGGER.error("this method is not supported");
        response.sendError(404);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        Site site = PageFilter.Static.getSite(request);

        AuthenticationSettings settings = AuthenticationFilter.getAuthenticator(request);
        AbstractAuthenticator authenticator = Optional.ofNullable(settings)
            .map(AuthenticationSettings::getAuthenticator)
            .orElse(null);

        if (authenticator == null) {
            throw new AuthenticationException("No Authenticator configured for URL: " + request.getRequestURL());
        }

        Session session = authenticator.login(request, response);

        if (session != null) {
            String redirect = DirectoryUtils.getCanonicalUrl(site, settings.getAuthenticatedLandingPage());
            if (StringUtils.isBlank(redirect)) {
                redirect = "/";
            }

            JspUtils.redirect(request, response, redirect);
            return;
        }

        response.sendError(404);
    }
}
