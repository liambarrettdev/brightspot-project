package com.brightspot.model.tag;

import java.util.Collections;
import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.tag.TagPageView;

public class TagPageViewModel extends AbstractViewModel<Tag> implements TagPageView {

    @Override
    public Object getName() {
        return model.getDisplayName();
    }

    @Override
    public Object getContent() {
        List<Object> items = model.getContents().isEmpty()
            ? getDefaultContent()
            : buildModuleViews(model.getContents());

        return buildConcatenatedView(items);
    }

    private List<Object> getDefaultContent() {
        return Collections.singletonList(buildListModuleView(model.getMostRecentContent()));
    }
}
