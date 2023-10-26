package com.brightspot.model.user;

import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.AbstractPageViewModel;
import com.brightspot.tool.SiteSingleton;
import com.psddev.cms.db.Site;
import com.psddev.cms.view.ViewBinding;

@ViewBinding(value = UserProfilePageViewModel.class, types = AbstractPageViewModel.MAIN_CONTENT_VIEW)
public class UserProfile extends AbstractPage implements SiteSingleton {

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        return "/profile";
    }
}
