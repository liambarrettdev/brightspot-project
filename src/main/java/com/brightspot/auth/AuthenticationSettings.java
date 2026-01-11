package com.brightspot.auth;

import com.brightspot.model.page.AbstractPage;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

/**
 * Configuration settings for authentication behavior on a site.
 *
 * <p>This embedded record is used to configure which authenticator to use and where to
 * redirect users after login and logout. It is typically stored in site settings via
 * {@link com.brightspot.tool.auth.AuthenticationSiteSettings}.</p>
 *
 * <p>The authenticator is required and determines how users authenticate. Landing pages
 * are optional and control where users are redirected after authentication actions.</p>
 *
 * @see AbstractAuthenticator
 * @see com.brightspot.tool.auth.AuthenticationSiteSettings
 */
@Recordable.Embedded
public class AuthenticationSettings extends Record {

    @Recordable.Required
    private AbstractAuthenticator authenticator;

    private AbstractPage authenticatedLandingPage;

    private AbstractPage unauthenticatedLandingPage;

    public AbstractAuthenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(AbstractAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public AbstractPage getAuthenticatedLandingPage() {
        return authenticatedLandingPage;
    }

    public void setAuthenticatedLandingPage(AbstractPage authenticatedLandingPage) {
        this.authenticatedLandingPage = authenticatedLandingPage;
    }

    public AbstractPage getUnauthenticatedLandingPage() {
        return unauthenticatedLandingPage;
    }

    public void setUnauthenticatedLandingPage(AbstractPage unauthenticatedLandingPage) {
        this.unauthenticatedLandingPage = unauthenticatedLandingPage;
    }
}
