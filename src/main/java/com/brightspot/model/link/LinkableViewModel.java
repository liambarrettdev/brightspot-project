package com.brightspot.model.link;

import com.brightspot.view.base.util.LinkView;
import com.psddev.cms.db.Site;
import com.psddev.cms.view.ViewModel;
import com.psddev.cms.view.servlet.CurrentSite;

public class LinkableViewModel extends ViewModel<Linkable> implements LinkView {

    @CurrentSite
    protected Site site;

    @Override
    public Object getBody() {
        return model.getLinkableText();
    }

    @Override
    public Object getHref() {
        return model.getLinkableUrl(site);
    }

    @Override
    public Object getTarget() {
        return null;
    }
}
