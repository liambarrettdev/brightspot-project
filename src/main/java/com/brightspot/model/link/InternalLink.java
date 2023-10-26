package com.brightspot.model.link;

import java.util.Optional;

import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.StringUtils;

@ToolUi.FieldDisplayOrder({
    "item",
    "text",
    "anchor",
    "target"
})
@Recordable.DisplayName("Internal")
public class InternalLink extends Link {

    @Required
    private Linkable item;

    @ToolUi.Tab(ADVANCED_TAB)
    private String anchor;

    public Linkable getItem() {
        return item;
    }

    public void setItem(Linkable item) {
        this.item = item;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    // -- Overrides -- //

    @Override
    public String getLinkUrl(Site site) {
        String url = Optional.ofNullable(getItem())
            .map(i -> i.getLinkableUrl(site))
            .orElse(null);

        String anchor = getAnchor();

        if (StringUtils.isBlank(anchor)) {
            return url;

        } else if (url != null) {
            return url + "#" + anchor;

        } else {
            return "#" + anchor;
        }
    }

    @Override
    public String getLinkTextFallback() {
        Linkable item = getItem();
        return item != null ? item.getLinkableText() : super.getLinkTextFallback();
    }

    // -- Static Methods -- //

    public static InternalLink create(Linkable linkable) {
        InternalLink instance = new InternalLink();
        instance.setItem(linkable);
        return instance;
    }
}
