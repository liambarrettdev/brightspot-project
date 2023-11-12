package com.brightspot.model.promo.list;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.promo.PromoModuleView;
import com.brightspot.view.model.promo.list.ListModuleView;

public class ListModuleViewModel extends AbstractViewModel<ListModule> implements ListModuleView {

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
            .map(item -> createView(PromoModuleView.class, item))
            .collect(Collectors.toList());
    }
}
