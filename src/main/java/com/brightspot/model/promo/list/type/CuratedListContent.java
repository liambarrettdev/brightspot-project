package com.brightspot.model.promo.list.type;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.model.list.ListContent;
import com.brightspot.model.promo.Promotable;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Curated")
public class CuratedListContent extends ListContent {

    private List<Promotable> items;

    public List<Promotable> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<Promotable> items) {
        this.items = items;
    }

    @Override
    public List<Promotable> getItems(Site site) {
        return getItems();
    }

    @Override
    public Integer getItemsPerPage() {
        return getItems().size();
    }
}
