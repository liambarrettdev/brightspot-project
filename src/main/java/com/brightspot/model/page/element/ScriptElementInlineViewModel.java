package com.brightspot.model.page.element;

import java.util.Optional;

import com.brightspot.view.base.page.InlineScriptView;
import com.psddev.cms.view.ViewModel;

public class ScriptElementInlineViewModel extends ViewModel<ScriptElement> implements InlineScriptView {

    @Override
    public Object getScript() {
        return Optional.of(model.getType())
            .filter(ScriptElement.InlineScript.class::isInstance)
            .map(ScriptElement.InlineScript.class::cast)
            .map(ScriptElement.InlineScript::getBody)
            .orElse(null);
    }
}
