package com.brightspot.model.quote;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.quote.QuoteModuleView;

public class QuoteModuleViewModel extends AbstractViewModel<QuoteModule> implements QuoteModuleView {

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
