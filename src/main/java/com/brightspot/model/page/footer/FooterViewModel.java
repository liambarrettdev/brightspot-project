package com.brightspot.model.page.footer;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.page.FooterView;

public class FooterViewModel extends AbstractViewModel<Footer> implements FooterView {

    @Override
    public Object getCopyright() {
        return Optional.ofNullable(model.getCopyright())
            .map(this::buildRichTextView)
            .orElse(null);
    }
}
