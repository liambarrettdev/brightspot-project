package com.brightspot.model.quote;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.project.view.base.util.ImageView;
import com.brightspot.project.view.model.quote.QuoteView;

public class QuoteModuleViewModel extends AbstractViewModel<QuoteModule> implements QuoteView {

    @Override
    public Object getQuote() {
        return model.getText();
    }

    @Override
    public Object getAttribution() {
        return model.getAttribution();
    }

    @Override
    public Object getImage() {
        return createView(ImageView.class, model.getImage());
    }
}
