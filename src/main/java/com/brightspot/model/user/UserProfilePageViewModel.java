package com.brightspot.model.user;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.servlet.LoginServlet;
import com.psddev.cms.view.ViewResponse;

public class UserProfilePageViewModel extends AbstractViewModel<UserProfile> {

    @Override
    protected void onCreate(ViewResponse response) {
        if (getUser() == null) {
            response.redirectTemporarily(LoginServlet.PATH);
        }
    }
}
