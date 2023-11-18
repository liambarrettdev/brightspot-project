package com.brightspot.model.page.element;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.psddev.dari.db.Record;

public class CustomHeadElements extends Record {

    @CollectionMinimum(1)
    private List<HeadElement> elements;

    //TODO add URL pattern matching options

    public List<HeadElement> getElements() {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        return elements;
    }

    public void setElements(List<HeadElement> elements) {
        this.elements = elements;
    }

    // -- Helper Methods -- //

    public <T extends HeadElement> List<T> getElements(Class<T> elementClass) {
        return getElements().stream()
            .filter(elementClass::isInstance)
            .map(elementClass::cast)
            .collect(Collectors.toList());
    }
}
