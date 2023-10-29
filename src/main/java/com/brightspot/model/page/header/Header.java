package com.brightspot.model.page.header;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.model.image.Image;
import com.brightspot.model.navigation.NavigationItem;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@ViewBinding(value = HeaderViewModel.class)
public class Header extends Record {

    @Required
    private String name;

    private String text;

    private Image image;

    @Recordable.Embedded
    private List<NavigationItem> navigationItems;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<NavigationItem> getNavigationItems() {
        if (navigationItems == null) {
            navigationItems = new ArrayList<>();
        }
        return navigationItems;
    }

    public void setNavigationItems(List<NavigationItem> navigationItems) {
        this.navigationItems = navigationItems;
    }
}
