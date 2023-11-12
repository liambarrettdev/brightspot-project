package com.brightspot.tool;

import com.psddev.dari.db.Recordable;

public interface Wrapper extends Recordable {

    String getViewType();

    default Object unwrap() {
        return this;
    }
}
