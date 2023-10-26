package com.brightspot.model.link;

import com.brightspot.utils.DirectoryUtils;
import com.psddev.cms.db.Site;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@ViewBinding(value = LinkableViewModel.class)
public interface Linkable extends Recordable {

    default String getLinkableUrl(Site site) {
        return DirectoryUtils.getCanonicalUrl(site, this);
    }

    String getLinkableText();
}
