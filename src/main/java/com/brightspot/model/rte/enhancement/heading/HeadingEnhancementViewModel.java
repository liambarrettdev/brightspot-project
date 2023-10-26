package com.brightspot.model.rte.enhancement.heading;

import com.brightspot.project.view.model.rte.RichTextHeadingView;
import com.psddev.cms.view.ViewModel;

public class HeadingEnhancementViewModel extends ViewModel<AbstractHeadingEnhancement> implements RichTextHeadingView {

    @Override
    public Object getTag() {
        return model.getTag();
    }

    @Override
    public Object getText() {
        return model.getText();
    }
}
