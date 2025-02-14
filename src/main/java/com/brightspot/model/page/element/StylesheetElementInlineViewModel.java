package com.brightspot.model.page.element;

import java.util.Optional;

import com.brightspot.view.base.page.InlineStylesheetView;
import com.psddev.cms.view.ViewModel;

public class StylesheetElementInlineViewModel extends ViewModel<StylesheetElement> implements InlineStylesheetView {

    @Override
    public Object getStyle() {
        return Optional.of(model.getType())
            .filter(StylesheetElement.InlineStylesheet.class::isInstance)
            .map(StylesheetElement.InlineStylesheet.class::cast)
            .map(StylesheetElement.InlineStylesheet::getBody)
            .orElse(null);
    }
}
