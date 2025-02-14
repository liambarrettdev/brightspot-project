package com.brightspot.model.page.footer;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.container.ColumnContainerModuleView;
import com.brightspot.view.model.page.footer.FooterView;

public class FooterViewModel extends AbstractViewModel<Footer> implements FooterView {

    @Override
    public Object getText() {
        return Optional.ofNullable(model.getContent())
            .map(this::buildRichTextView)
            .orElse(null);
    }

    @Override
    public Object getLinks() {
        return createView(ColumnContainerModuleView.class, model.getLinks());
    }

    @Override
    public Object getCopyright() {
        return Optional.ofNullable(model.getCopyright())
            .map(this::buildRichTextView)
            .orElse(null);
    }
}
