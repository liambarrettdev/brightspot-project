package com.brightspot.model.page.element;

import com.brightspot.view.base.page.MetaView;
import com.psddev.cms.view.ViewModel;

public class MetaElementViewModel extends ViewModel<MetaElement> implements MetaView {

    @Override
    public Object getName() {
        return model.getName();
    }

    @Override
    public Object getHttpEquiv() {
        return model.getHttpEquiv();
    }

    @Override
    public Object getContent() {
        return model.getContent();
    }
}
