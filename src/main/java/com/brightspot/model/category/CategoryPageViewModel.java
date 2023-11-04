package com.brightspot.model.category;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.category.CategoryPageView;

public class CategoryPageViewModel extends AbstractViewModel<Category> implements CategoryPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Object getRelatedContent() {
        return buildPromoListView(model.getMostRecentContent());
    }
}
