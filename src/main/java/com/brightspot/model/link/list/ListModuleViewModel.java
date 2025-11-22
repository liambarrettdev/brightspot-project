package com.brightspot.model.link.list;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.LinkView;
import com.brightspot.view.model.link.list.ListModuleView;

public class ListModuleViewModel extends AbstractViewModel<ListModule> implements ListModuleView {

    @Override
    public Collection<?> getItems() {
        return model.getItems().stream()
            .map(link -> createView(LinkView.class, link))
            .collect(Collectors.toList());
    }
}
