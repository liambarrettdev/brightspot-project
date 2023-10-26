package com.brightspot.model.form.field.choice;

import java.util.Optional;

import com.brightspot.model.form.field.FieldType;
import com.brightspot.project.view.model.form.input.SelectInputView;

public class SelectInputViewModel extends ChoiceFieldViewModel implements SelectInputView {

    @Override
    public Boolean getMultiple() {
        return Optional.ofNullable(model.getType())
            .map(ChoiceFieldType::isAllowMultiSelect)
            .orElse(null);
    }

    @Override
    public Object getPlaceholder() {
        return Optional.ofNullable(model.getType())
            .map(FieldType::getPlaceholder)
            .orElse(null);
    }

    @Override
    public Boolean getRequired() {
        return Optional.ofNullable(model.getType())
            .map(FieldType::isRequired)
            .orElse(null);
    }
}
