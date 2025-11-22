package com.brightspot.model.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ConcatenatedView;

public class PageMainViewModel extends AbstractViewModel<Page> implements ConcatenatedView {

    @Override
    public Collection<?> getItems() {
        List<Object> items = new ArrayList<>();

        Optional.ofNullable(model.getContents())
            .map(this::buildModuleViews)
            .ifPresent(items::addAll);

        return items;
    }
}
