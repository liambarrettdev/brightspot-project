package com.brightspot.model.blog;

import java.util.Collection;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.blog.BlogPageView;

public class BlogPageViewModel extends AbstractViewModel<Blog> implements BlogPageView {

    @Override
    public Collection<?> getPosts() {
        return BlogPageView.super.getPosts();
    }
}
