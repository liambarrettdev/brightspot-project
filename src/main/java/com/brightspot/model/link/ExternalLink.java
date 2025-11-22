package com.brightspot.model.link;

import com.brightspot.tool.field.annotation.Url;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Recordable;

@ToolUi.FieldDisplayOrder({
    "text",
    "url",
    "anchor",
    "target"
})
@Recordable.DisplayName("External")
public class ExternalLink extends Link {

    public ExternalLink() {
        super();
        this.setTarget(Target.NEW);
    }

    @Url
    @Recordable.Required
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // -- Overrides -- //

    @Override
    public String getLinkUrl(Site site) {
        return getUrl();
    }

    // -- Static Methods -- //

    public static ExternalLink create(String text, String url) {
        ExternalLink instance = new ExternalLink();
        instance.setText(text);
        instance.setUrl(url);
        return instance;
    }
}
