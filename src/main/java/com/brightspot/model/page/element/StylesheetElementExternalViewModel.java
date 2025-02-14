package com.brightspot.model.page.element;

import java.util.Optional;

import com.brightspot.view.base.page.ExternalStylesheetView;
import com.psddev.cms.view.ViewModel;

public class StylesheetElementExternalViewModel extends ViewModel<StylesheetElement> implements ExternalStylesheetView {

    @Override
    public Object getHref() {
        return Optional.of(model.getType())
            .filter(StylesheetElement.ExternalStylesheet.class::isInstance)
            .map(StylesheetElement.ExternalStylesheet.class::cast)
            .map(StylesheetElement.ExternalStylesheet::getLink)
            .orElse(null);
    }
}
