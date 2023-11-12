package com.brightspot.model.category;

import java.util.Collection;
import java.util.Collections;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ConcatenatedView;
import com.brightspot.view.model.category.CategoryPageView;

public class CategoryPageViewModel extends AbstractViewModel<Category> implements CategoryPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Object getContent() {
        return new ConcatenatedView.Builder()
            .addAllToItems(model.getContents().isEmpty() ? getDefaultContent() : buildModuleViews(model.getContents()))
            .build();
    }

    private Collection<?> getDefaultContent() {
        return Collections.singleton(buildListModuleView(model.getMostRecentContent()));
    }
}
