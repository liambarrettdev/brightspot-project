package com.brightspot.model.category;

import java.util.Collections;
import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.category.CategoryPageView;

public class CategoryPageViewModel extends AbstractViewModel<Category> implements CategoryPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Object getContent() {
        List<Object> items = model.getContents().isEmpty()
            ? getDefaultContent()
            : buildModuleViews(model.getContents());

        return buildConcatenatedView(items);
    }

    private List<Object> getDefaultContent() {
        return Collections.singletonList(buildListModuleView(model.getMostRecentContent()));
    }
}
