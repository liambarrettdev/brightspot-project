package com.brightspot.model.form.field;

import java.util.ArrayList;
import java.util.List;

import com.psddev.cms.view.ViewBinding;

@ViewBinding(value = FieldSetViewModel.class, types = FieldSet.VIEW_CLASS)
public class FieldSet extends Field {

    protected static final String VIEW_CLASS = "field-set";

    @Types(FieldInput.class)
    @CollectionMinimum(1)
    private List<FieldInput> fields;

    public List<FieldInput> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<FieldInput> fields) {
        this.fields = fields;
    }

    // -- Overrides -- //

    @Override
    public String getViewClass() {
        return VIEW_CLASS;
    }
}
