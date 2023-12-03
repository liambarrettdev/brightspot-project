package com.brightspot.model.user;

import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.PageMainViewModel;
import com.brightspot.tool.SiteSingleton;
import com.psddev.cms.db.Site;
import com.psddev.cms.view.ViewBinding;

@ViewBinding(value = UserProfilePageViewModel.class, types = PageMainViewModel.MAIN_CONTENT_VIEW)
public class UserProfilePage extends AbstractPage implements SiteSingleton {

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        return "/profile";
    }
}
