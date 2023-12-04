package com.brightspot.model.form.field.choice;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.view.model.form.input.option.OptionView;
import com.psddev.cms.view.ViewModel;

public class ChoiceFieldViewModel extends ViewModel<ChoiceField> {

    public Object getLabel() {
        return model.getName();
    }

    public Object getName() {
        return model.getParameterName();
    }

    public Collection<?> getOptions() {
        return model.getChoices().stream()
            .map(this::buildOptionView)
            .collect(Collectors.toList());
    }

    // -- Utility Methods -- //

    private OptionView buildOptionView(Choice choice) {
        return new OptionView.Builder()
            .text(choice.getText())
            .value(choice.getValue())
            .build();
    }
}
