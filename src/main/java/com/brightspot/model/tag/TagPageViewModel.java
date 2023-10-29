package com.brightspot.model.tag;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.promo.PromoView;
import com.brightspot.view.model.tag.TagPageView;

public class TagPageViewModel extends AbstractViewModel<Tag> implements TagPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Collection<?> getContent() {
        return model.getMostRecentContent().stream()
            .map(item -> createView(PromoView.class, item))
            .collect(Collectors.toList());
    }
}
