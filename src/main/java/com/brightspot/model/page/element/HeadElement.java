package com.brightspot.model.page.element;

import com.brightspot.tool.Wrapper;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
public abstract class HeadElement extends Record implements Wrapper {

    @Required
    private String internalName;

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }
}
