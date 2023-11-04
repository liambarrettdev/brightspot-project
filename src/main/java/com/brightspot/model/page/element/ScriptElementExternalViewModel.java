package com.brightspot.model.page.element;

import java.util.Optional;

import com.brightspot.view.base.page.ExternalScriptView;
import com.psddev.cms.view.ViewModel;

public class ScriptElementExternalViewModel extends ViewModel<ScriptElement> implements ExternalScriptView {

    @Override
    public Object getSrc() {
        return Optional.of(model.getType())
            .filter(ScriptElement.ExternalScript.class::isInstance)
            .map(ScriptElement.ExternalScript.class::cast)
            .map(ScriptElement.ExternalScript::getSrc)
            .orElse(null);
    }

    @Override
    public Boolean getAsync() {
        return Optional.of(model.getType())
            .filter(ScriptElement.ExternalScript.class::isInstance)
            .map(ScriptElement.ExternalScript.class::cast)
            .map(ScriptElement.ExternalScript::isAsync)
            .orElse(null);
    }

    @Override
    public Boolean getDefer() {
        return Optional.of(model.getType())
            .filter(ScriptElement.ExternalScript.class::isInstance)
            .map(ScriptElement.ExternalScript.class::cast)
            .map(ScriptElement.ExternalScript::isDefer)
            .orElse(null);
    }
}
