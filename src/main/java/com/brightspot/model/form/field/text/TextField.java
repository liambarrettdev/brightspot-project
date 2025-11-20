package com.brightspot.model.form.field.text;

import com.brightspot.model.form.field.FieldInput;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@ViewBinding(value = TextInputViewModel.class, types = TextField.VIEW_TYPE)
public class TextField extends FieldInput {

    protected static final String VIEW_TYPE = "text-field";

    @Recordable.Required
    private TextFieldType type = new TextFieldType.Default();

    public TextFieldType getType() {
        return type;
    }

    public void setType(TextFieldType type) {
        this.type = type;
    }

    // -- Overrides -- //

    @Override
    public String getViewClass() {
        return VIEW_TYPE;
    }
}
