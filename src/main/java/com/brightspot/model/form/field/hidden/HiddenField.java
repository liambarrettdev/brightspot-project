package com.brightspot.model.form.field.hidden;

import com.brightspot.model.form.field.FieldInput;
import com.psddev.cms.view.ViewBinding;

@ViewBinding(value = HiddenInputViewModel.class, types = HiddenField.VIEW_CLASS)
public class HiddenField extends FieldInput {

    protected static final String VIEW_CLASS = "hidden-field";

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // -- Overrides -- //

    @Override
    public String getViewClass() {
        return VIEW_CLASS;
    }
}
