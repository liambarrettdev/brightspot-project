package com.brightspot.model.page;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.model.module.AbstractModule;
import com.psddev.cms.db.Site;
import com.psddev.cms.view.ViewBinding;

@ViewBinding(value = PageViewModel.class, types = { AbstractPageViewModel.MAIN_CONTENT_VIEW })
public class Page extends AbstractPage {

    private List<AbstractModule> contents;

    public List<AbstractModule> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return contents;
    }

    public void setContents(List<AbstractModule> contents) {
        this.contents = contents;
    }

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        return null;
    }
}
