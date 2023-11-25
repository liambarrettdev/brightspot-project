package com.brightspot.model.blog;

import java.util.Collection;

import com.brightspot.model.page.AbstractPageViewModel;
import com.brightspot.view.model.blog.BlogPageView;

public class BlogPageViewModel extends AbstractPageViewModel<Blog> implements BlogPageView {

    @Override
    public Collection<?> getPosts() {
        return BlogPageView.super.getPosts();
    }
}
