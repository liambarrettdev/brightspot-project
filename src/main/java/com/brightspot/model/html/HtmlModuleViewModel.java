package com.brightspot.model.html;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.html.HtmlContentView;

public class HtmlModuleViewModel extends AbstractViewModel<HtmlModule> implements HtmlContentView {

    @Override
    public Object getRawHtml() {
        return Optional.ofNullable(model.getRawHtml())
            .map(HtmlModule::of)
            .orElse(null);
    }
}
