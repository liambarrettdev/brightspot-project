package com.brightspot.auth;

import com.brightspot.model.page.AbstractPage;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

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
