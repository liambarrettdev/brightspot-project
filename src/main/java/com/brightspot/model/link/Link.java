package com.brightspot.model.link;

import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.Embedded
@ViewBinding(value = LinkViewModel.class)
public abstract class Link extends Record {

    public static final String ADVANCED_TAB = "Advanced";

    public abstract String getLinkUrl(Site site);

    @ToolUi.Placeholder(dynamicText = "${content.getLinkTextFallback()}", editable = true)
    private String text;

    @ToolUi.Placeholder("Same Window/Tab")
    @ToolUi.Tab(ADVANCED_TAB)
    private Target target;

    public String getText() {
        return ObjectUtils.firstNonBlank(text, getLinkTextFallback());
    }

    public void setText(String text) {
        this.text = text;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    // -- Helper Methods -- //

    public String getLinkTextFallback() {
        return null;
    }

    // - Enums -- //

    public enum Target {

        NEW("New Window/Tab", "_blank");

        private final String label;
        private final String value;

        Target(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
