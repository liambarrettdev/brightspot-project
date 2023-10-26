package com.brightspot.model.form;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.model.form.field.Field;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Form")
@ViewBinding(value = FormModuleViewModel.class, types = FormModule.VIEW_TYPE)
public class FormModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_TYPE = "form-module";

    private String name;

    @CollectionMinimum(1)
    private List<Field> fields;

    private String endpoint;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public String getLabel() {
        return getName();
    }
}
