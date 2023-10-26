package com.brightspot.model.form;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.form.FormView;

public class FormModuleViewModel extends AbstractViewModel<FormModule> implements FormView {

    @Override
    public Object getTitle() {
        return model.getName();
    }

    @Override
    public Collection<?> getInputs() {
        return model.getFields().stream()
            .map(input -> createView(input.getViewClass(), input))
            .collect(Collectors.toList());
    }

    @Override
    public Object getAction() {
        return model.getEndpoint();
    }

    @Override
    public Object getMethod() {
        return "POST";
    }
}
