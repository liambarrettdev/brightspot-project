package com.brightspot.model.navigation;

import java.util.ArrayList;
import java.util.List;

import com.psddev.cms.db.Site;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@ViewBinding(value = NavigationItemViewModel.class)
public interface NavigationItem extends Recordable {

    String getCtaText();

    default String getCtaUrl(Site site) {
        return null;
    }

    default List<NavigationItem> getItems() {
        return new ArrayList<>();
    }
}
