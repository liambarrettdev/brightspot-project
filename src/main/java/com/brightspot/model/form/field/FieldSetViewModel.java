package com.brightspot.model.form.field;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.project.view.model.form.FieldSetView;
import com.psddev.cms.view.ViewModel;

public class FieldSetViewModel extends ViewModel<FieldSet> implements FieldSetView {

    @Override
    public Collection<?> getInputs() {
        return model.getFields().stream()
            .map(input -> createView(input.getViewClass(), input))
            .collect(Collectors.toList());
    }
}
