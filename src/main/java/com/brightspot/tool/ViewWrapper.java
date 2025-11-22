package com.brightspot.tool;

import com.psddev.dari.db.Recordable;

public interface ViewWrapper extends Recordable {

    String getViewType();

    default Object unwrap() {
        return this;
    }
}
