package com.brightspot.model.link.list;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.model.link.Link;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("List (Links)")
@ViewBinding(value = ListModuleViewModel.class, types = ListModule.VIEW_CLASS)
public class ListModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_CLASS = "link-list-module";

    private List<Link> items;

    public List<Link> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<Link> items) {
        this.items = items;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_CLASS;
    }
}
