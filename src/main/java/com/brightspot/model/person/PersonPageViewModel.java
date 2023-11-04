package com.brightspot.model.person;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.person.PersonPageView;

public class PersonPageViewModel extends AbstractViewModel<Person> implements PersonPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Object getPosition() {
        return model.getPosition();
    }

    @Override
    public Object getEmail() {
        return model.getEmail();
    }

    @Override
    public Object getWebsite() {
        return model.getWebsite();
    }

    @Override
    public Object getImage() {
        return createView(ImageView.class, model.getAvatar());
    }

    @Override
    public Object getBiography() {
        return buildRichTextView(model.getBiography());
    }

    @Override
    public Object getRelatedContent() {
        return buildPromoListView(model.getMostRecentContent());
    }
}
