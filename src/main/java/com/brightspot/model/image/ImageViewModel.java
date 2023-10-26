package com.brightspot.model.image;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.project.view.base.util.ImageView;
import com.brightspot.project.view.base.util.LinkView;
import com.psddev.dari.util.StorageItem;

public class ImageViewModel extends AbstractViewModel<Image> implements ImageView {

    @Override
    public Object getSrc() {
        return Optional.ofNullable(model.getFile())
            .map(StorageItem::getPublicUrl)
            .orElse(null);
    }

    @Override
    public Object getAlt() {
        return model.getAltText();
    }

    @Override
    public Object getCaption() {
        return model.getCaption();
    }

    @Override
    public Object getCredit() {
        return model.getCredit();
    }

    @Override
    public Object getLink() {
        return createView(LinkView.class, model.getLink());
    }
}
