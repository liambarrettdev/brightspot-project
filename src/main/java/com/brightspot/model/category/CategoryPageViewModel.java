package com.brightspot.model.category;

import java.util.Collection;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.category.CategoryPageView;

public class CategoryPageViewModel extends AbstractViewModel<Category> implements CategoryPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Collection<?> getContent() {
        return CategoryPageView.super.getContent();
    }
}
