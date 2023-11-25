package com.brightspot.model.blog;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brightspot.model.page.AbstractPageViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.base.util.LinkView;
import com.brightspot.view.model.blog.BlogPostPageView;

public class BlogPostPageViewModel extends AbstractPageViewModel<BlogPost> implements BlogPostPageView {

    @Override
    public Object getLead() {
        return createView(ImageView.class, model.getLeadImage());
    }

    @Override
    public Object getHeadline() {
        return model.getHeadline();
    }

    @Override
    public Object getSubHeadline() {
        return model.getSubHeadline();
    }

    @Override
    public Object getByline() {
        return Optional.ofNullable(model.asAuthorableData().getAuthor())
            .map(author -> createView(LinkView.class, author))
            .orElse(null);
    }

    @Override
    public Object getDatePublished() {
        return model.getPublishDate().toString();
    }

    @Override
    public Object getDateModified() {
        return BlogPostPageView.super.getDateModified();
    }

    @Override
    public Object getBody() {
        return Optional.ofNullable(model.getBody())
            .map(this::buildObjectView)
            .orElse(null);
    }

    @Override
    public Collection<?> getTags() {
        return model.asTaggableData().getTags().stream()
            .map(tag -> createView(LinkView.class, tag))
            .collect(Collectors.toList());
    }
}
