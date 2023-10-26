package com.brightspot.model.rte.enhancement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.brightspot.utils.Utils;
import com.psddev.cms.db.RichTextElement;
import com.psddev.dari.util.ObjectUtils;

public abstract class AbstractRichTextEnhancement extends RichTextElement {

    public static final String MENU_GROUP = "Enhancements";

    private static final String STATE_ATTRIBUTE = "data-state";

    protected abstract String getElementName();

    // -- Overrides -- //

    @Override
    public void fromAttributes(Map<String, String> attributes) {
        if (attributes != null) {
            String stateString = attributes.get(STATE_ATTRIBUTE);
            if (stateString != null) {
                Map<String, Object> simpleValues = Utils.uncheckedCast(ObjectUtils.fromJson(stateString));
                getState().setValues(simpleValues);
            }
        }
    }

    @Override
    public Map<String, String> toAttributes() {
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put(STATE_ATTRIBUTE, ObjectUtils.toJson(getState().getSimpleValues()));
        return attributes;
    }

    @Override
    public void fromBody(String body) {
        // do nothing
    }

    @Override
    public String toBody() {
        return Optional.ofNullable(getElementName())
            .map(name -> String.format("[%s]", name))
            .orElse(null);
    }
}
