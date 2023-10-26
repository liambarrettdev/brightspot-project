package com.brightspot.model.form.field.text;

import java.util.Optional;

import com.brightspot.model.form.field.FieldType;
import com.brightspot.view.model.form.input.TextInputView;
import com.psddev.cms.view.ViewModel;

public class TextInputViewModel extends ViewModel<TextField> implements TextInputView {

    @Override
    public Object getLabel() {
        return model.getName();
    }

    @Override
    public Object getName() {
        return model.getParameterName();
    }

    @Override
    public Object getRegex() {
        return Optional.ofNullable(model.getType())
            .map(TextFieldType::getRegexPattern)
            .orElse(null);
    }

    @Override
    public Object getType() {
        return Optional.ofNullable(model.getType())
            .map(FieldType::getFieldType)
            .orElse(TextFieldType.Default.FIELD_TYPE);
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
