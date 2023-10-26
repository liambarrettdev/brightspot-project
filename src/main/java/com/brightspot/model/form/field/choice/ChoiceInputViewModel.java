package com.brightspot.model.form.field.choice;

import java.util.Optional;

import com.brightspot.model.form.field.FieldType;
import com.brightspot.project.view.model.form.input.ChoiceInputView;

public class ChoiceInputViewModel extends ChoiceFieldViewModel implements ChoiceInputView {

    @Override
    public Object getType() {
        return Optional.ofNullable(model.getType())
            .map(FieldType::getFieldType)
            .orElse(null);
    }

    @Override
    public Boolean getRequired() {
        return Optional.ofNullable(model.getType())
            .map(FieldType::isRequired)
            .orElse(null);
    }
}
