package com.brightspot.model.page.element;

import com.brightspot.view.base.page.LinkView;
import com.psddev.cms.view.ViewModel;

public class LinkElementViewModel extends ViewModel<LinkElement> implements LinkView {

    @Override
    public Object getRel() {
        return model.getRel();
    }

    @Override
    public Object getHref() {
        return model.getHref();
    }

    @Override
    public Object getAsAttribute() {
        return model.getAsAttribute();
    }

    @Override
    public Boolean getCrossOrigin() {
        return model.isCrossOrigin();
    }
}
