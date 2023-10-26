package com.brightspot.model.link;

import java.util.Optional;

import com.brightspot.project.view.base.util.LinkView;
import com.psddev.cms.db.Site;
import com.psddev.cms.view.ViewModel;
import com.psddev.cms.view.servlet.CurrentSite;

public class LinkViewModel extends ViewModel<Link> implements LinkView {

    @CurrentSite
    protected Site site;

    @Override
    public Object getBody() {
        return model.getText();
    }

    @Override
    public Object getHref() {
        return model.getLinkUrl(site);
    }

    @Override
    public Object getTarget() {
        return Optional.ofNullable(model.getTarget())
            .map(Link.Target::getValue)
            .orElse(null);
    }
}
