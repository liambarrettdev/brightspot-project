package com.brightspot.model.tag;

import java.util.Collection;
import java.util.Collections;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ConcatenatedView;
import com.brightspot.view.model.tag.TagPageView;

public class TagPageViewModel extends AbstractViewModel<Tag> implements TagPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Object getContent() {
        return new ConcatenatedView.Builder()
            .addAllToItems(model.getContents().isEmpty() ? getDefaultContent() : buildModuleViews(model.getContents()))
            .build();
    }

    private Collection<?> getDefaultContent() {
        return Collections.singleton(buildListModuleView(model.getMostRecentContent()));
    }
}
