package com.brightspot.model.navigation;

import java.util.Optional;

import com.brightspot.model.link.InternalLink;
import com.brightspot.model.link.Link;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.DisplayName("Link Item")
public class NavigationLinkItem extends Record implements NavigationItem {

    @ToolUi.Placeholder(dynamicText = "${content.getTextFallback()}", editable = true)
    private String text;

    @Required
    private Link link = new InternalLink();

    public String getText() {
        return ObjectUtils.firstNonBlank(text, getTextFallback());
    }

    public void setText(String text) {
        this.text = text;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    // -- Overrides -- //

    @Override
    public String getCtaText() {
        return getText();
    }

    @Override
    public String getCtaUrl(Site site) {
        return Optional.ofNullable(link)
            .map(link -> link.getLinkUrl(site))
            .orElse(null);
    }

    @Override
    public String getLabel() {
        return getCtaText();
    }

    // -- Helper Methods -- //

    public String getTextFallback() {
        return Optional.ofNullable(link)
            .map(Link::getText)
            .orElse(null);
    }
}
