package com.brightspot.model.module;

import java.util.Optional;

import com.psddev.dari.db.Recordable;

public class SharedModule extends AbstractModule {

    @Recordable.Required
    private Module content;

    public Module getContent() {
        return content;
    }

    public void setContent(Module content) {
        this.content = content;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return Optional.ofNullable(getContent())
            .map(Module::getModule)
            .map(AbstractModule::getViewType)
            .orElse(null);
    }

    @Override
    public Object unwrap() {
        return Optional.ofNullable(getContent())
            .map(Module::getModule)
            .orElse(null);
    }

    @Override
    public String getLabel() {
        return Optional.ofNullable(getContent())
            .map(Module::getName)
            .orElse(null);
    }
}
