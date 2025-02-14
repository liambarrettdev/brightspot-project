package com.brightspot.servlet;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.auth.AbstractAuthenticator;
import com.brightspot.auth.AuthenticationFilter;
import com.brightspot.auth.AuthenticationSettings;
import com.brightspot.auth.PasswordAuthenticator;
import com.brightspot.auth.Session;
import com.brightspot.auth.Token;
import com.brightspot.model.user.User;
import com.brightspot.tool.email.CustomMailMessage;
import com.brightspot.utils.DirectoryUtils;
import com.brightspot.utils.RichTextUtils;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.JspUtils;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.Password;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationServlet.class);

    public static final String SERVLET_PATH = "_auth/register";

    public static final String PARAM_TOKEN = "token";
    public static final String PARAM_FORENAME = "forename";
    public static final String PARAM_SURNAME = "surname";

    // -- Overrides -- //

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Site site = PageFilter.Static.getSite(request);

        AuthenticationSettings settings = AuthenticationFilter.getAuthenticator(request);
        AbstractAuthenticator authenticator = settings.getAuthenticator();

        // prepare response
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setStatus(HttpServletResponse.SC_OK);

        String emailParam = request.getParameter(PasswordAuthenticator.PARAM_EMAIL);
        String passwordParam = request.getParameter(PasswordAuthenticator.PARAM_PASSWORD);

        if (StringUtils.isAnyBlank(emailParam, passwordParam)) {
            LOGGER.warn("Request missing required parameters");
            throw new ServletException("Email and Password are required");
        }

        if (Query.from(User.class).where("email = ?", emailParam).hasMoreThan(0)) {
            LOGGER.warn("User already exists: {}", emailParam);
            throw new ServletException("User with this email already exists");
        }

        // create new user
        User user = new User();

        // set required fields
        user.setEmail(emailParam);
        user.setStatus(User.Status.PENDING);
        user.asPasswordUserData().setPassword(Password.create(passwordParam));

        // set optional fields
        Optional.ofNullable(request.getParameter(PARAM_FORENAME)).ifPresent(user::setFirstName);
        Optional.ofNullable(request.getParameter(PARAM_SURNAME)).ifPresent(user::setLastName);

        user.save();

        // send validation token
        Token token = Token.createToken(user);

        String registrationLink = generateRegistrationUrl(site, token);

        new CustomMailMessage()
            .to(emailParam)
            .from(authenticator.getFrom())
            .subject(authenticator.getSubject())
            .bodyHtml(processEmailTemplate(authenticator.getTemplate(), registrationLink))
            .send();

        JspUtils.redirect(request, response, "/");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Site site = PageFilter.Static.getSite(request);

        AuthenticationSettings settings = AuthenticationFilter.getAuthenticator(request);

        // prepare response
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setStatus(HttpServletResponse.SC_OK);

        // find request params
        String tokenId = request.getParameter(PARAM_TOKEN);

        // complete registration
        Token token = Query.from(Token.class).where("id = ?", tokenId).first();

        if (ObjectUtils.isBlank(token)) {
            //TODO handle error
            return;
        }

        User user = Optional.ofNullable(token.getUser())
            .filter(User.class::isInstance)
            .map(User.class::cast)
            .orElse(null);

        if (ObjectUtils.isBlank(user)) {
            //TODO handle error
            return;
        }

        user.setStatus(User.Status.VERIFIED);
        user.save();
        token.delete();

        // log in user after successfully completing registration
        Session session = AbstractAuthenticator.addUserSession(request, response, user);

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

    private String generateRegistrationUrl(Site site, Token token) {
        if (site == null || token == null) {
            return null;
        }

        String uri = String.format("%s/%s", site.getPrimaryUrl(), SERVLET_PATH);

        return Utils.addQueryParameters(uri,
            PARAM_TOKEN, token.getId()
        );
    }

    private String processEmailTemplate(String template, String link) {
        if (StringUtils.isBlank(template) || ObjectUtils.isBlank(link)) {
            return null;
        }

        return RichTextUtils.richTextToPlainText(template.replace(AbstractAuthenticator.KEY_LINK, link));
    }
}
