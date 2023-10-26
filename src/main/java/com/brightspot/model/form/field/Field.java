package com.brightspot.model.form.field;

import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
public abstract class Field extends Record {

    public abstract String getViewClass();
}
