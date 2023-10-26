package com.brightspot.model.promo.list;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.project.view.model.promo.PromoView;
import com.brightspot.project.view.model.promo.list.ListView;

public class ListModuleViewModel extends AbstractViewModel<ListModule> implements ListView {

    @Override
    public Object getTitle() {
        return model.getTitle();
    }

    @Override
    public Object getDescription() {
        return buildRichTextView(model.getDescription());
    }

    @Override
    public Collection<?> getItems() {
        return model.getItems().stream()
            .map(item -> createView(PromoView.class, item))
            .collect(Collectors.toList());
    }
}
