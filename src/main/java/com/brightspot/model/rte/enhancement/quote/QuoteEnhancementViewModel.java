package com.brightspot.model.rte.enhancement.quote;

import java.util.Optional;

import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.view.base.EnhancementView;
import com.brightspot.view.model.quote.QuoteModuleView;
import com.psddev.cms.view.ViewModel;

public class QuoteEnhancementViewModel extends ViewModel<QuoteEnhancement> implements EnhancementView {

    @Override
    public Object getAlignment() {
        return Optional.ofNullable(model.asAlignableData())
            .map(Alignable.Data::getAlignment)
            .map(Alignable.Data.Alignment::getName)
            .orElse(null);
    }

    @Override
    public Object getContent() {
        return createView(QuoteModuleView.class, model.getQuote());
    }
}
