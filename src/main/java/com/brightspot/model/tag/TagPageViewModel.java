package com.brightspot.model.tag;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.tag.TagPageView;

public class TagPageViewModel extends AbstractViewModel<Tag> implements TagPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Object getRelatedContent() {
        return buildPromoListView(model.getMostRecentContent());
    }
}
