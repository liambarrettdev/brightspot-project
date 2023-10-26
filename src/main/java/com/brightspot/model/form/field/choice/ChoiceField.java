package com.brightspot.model.form.field.choice;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.model.form.field.FieldInput;
import com.psddev.cms.view.ViewBinding;

@ViewBinding(value = ChoiceFieldDelegateViewModel.class, types = ChoiceField.VIEW_CLASS)
public class ChoiceField extends FieldInput {

    protected static final String VIEW_CLASS = "choice-field";

    @Required
    private ChoiceFieldType type = new ChoiceFieldType.Dropdown();

    @CollectionMinimum(1)
    private List<Choice> choices;

    public ChoiceFieldType getType() {
        return type;
    }

    public void setType(ChoiceFieldType type) {
        this.type = type;
    }

    public List<Choice> getChoices() {
        if (choices == null) {
            choices = new ArrayList<>();
        }
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    // -- Overrides -- //

    @Override
    public String getViewClass() {
        return VIEW_CLASS;
    }
}
