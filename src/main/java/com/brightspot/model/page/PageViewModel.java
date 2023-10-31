package com.brightspot.model.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ConcatenatedView;

public class PageViewModel extends AbstractViewModel<Page> implements ConcatenatedView {

    @Override
    public Collection<?> getItems() {
        List<Object> items = new ArrayList<>();

        Optional.ofNullable(model.getContents())
            .map(List::stream)
            .map(stream -> stream.map(this::buildWrappedObjectView)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))
            .ifPresent(items::addAll);

        return items;
    }
}
