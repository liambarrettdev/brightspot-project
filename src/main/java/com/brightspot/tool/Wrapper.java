package com.brightspot.tool;

public interface Wrapper {

    String getViewType();

    default Object unwrap() {
        return this;
    }
}
