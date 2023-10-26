package com.brightspot.model.article;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.project.view.base.util.ConcatenatedView;

public class ArticlePageViewModel extends AbstractViewModel<Article> implements ConcatenatedView {

    @Override
    public Collection<?> getItems() {
        List<Object> items = new ArrayList<>();

        Optional.ofNullable(model.getBody())
            .map(this::buildModuleView)
            .ifPresent(items::add);

        return items;
    }
}
