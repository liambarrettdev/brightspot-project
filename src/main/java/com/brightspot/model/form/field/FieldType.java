package com.brightspot.model.form.field;

import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
public abstract class FieldType extends Record {

    public abstract String getFieldType();

    public String getPlaceholder() {
        return null;
    }

    public Boolean isRequired() {
        return false;
    }
}
