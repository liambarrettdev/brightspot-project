package com.brightspot.model.navigation;

import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Text Item")
public class NavigationTextItem extends Record implements NavigationItem {

    @Recordable.Required
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // -- Overrides -- //

    @Override
    public String getCtaText() {
        return getText();
    }

    @Override
    public String getLabel() {
        return getCtaText();
    }
}
