package com.brightspot.model.article;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.person.Person;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.base.util.LinkView;
import com.brightspot.view.model.article.ArticlePageView;

public class ArticlePageViewModel extends AbstractViewModel<Article> implements ArticlePageView {

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
            .map(Person::getDisplayName)
            .orElse(null);
    }

    @Override
    public Object getPublishDate() {
        return model.getPublishDate().toString();
    }

    @Override
    public Object getBody() {
        return Optional.ofNullable(model.getBody())
            .map(this::buildModuleView)
            .orElse(null);
    }

    @Override
    public Collection<?> getTags() {
        return model.asTaggableData().getTags().stream()
            .map(tag -> createView(LinkView.class, tag))
            .collect(Collectors.toList());
    }
}
