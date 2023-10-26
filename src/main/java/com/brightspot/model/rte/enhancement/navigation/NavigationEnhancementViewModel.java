package com.brightspot.model.rte.enhancement.navigation;

import com.brightspot.project.view.base.util.LinkView;
import com.brightspot.project.view.model.rte.RichTextNavigationView;
import com.psddev.cms.view.ViewModel;

public class NavigationEnhancementViewModel extends ViewModel<NavigationEnhancement> implements RichTextNavigationView {

    @Override
    public Object getTitle() {
        return model.getTitle();
    }

    @Override
    public Object getPreviousPage() {
        return createView(LinkView.class, model.getPrevious());
    }

    @Override
    public Object getNextPage() {
        return createView(LinkView.class, model.getNext());
    }
}
