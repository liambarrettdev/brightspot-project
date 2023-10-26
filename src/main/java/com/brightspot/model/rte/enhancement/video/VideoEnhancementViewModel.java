package com.brightspot.model.rte.enhancement.video;

import com.brightspot.model.rte.enhancement.image.ImageEnhancement;
import com.brightspot.view.base.EnhancementView;
import com.psddev.cms.view.ViewModel;

public class VideoEnhancementViewModel extends ViewModel<ImageEnhancement> implements EnhancementView {

    @Override
    public Object getAlignment() {
        return EnhancementView.super.getAlignment();
    }

    @Override
    public Object getContent() {
        return EnhancementView.super.getContent();
    }
}
