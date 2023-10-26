package com.brightspot.model.image;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.brightspot.model.link.Link;
import com.brightspot.tool.field.annotation.MimeTypes;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Directory;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.StorageItem;

@Content.PreviewField("file")
@ToolUi.IconName(Image.ICON_NAME)
@ToolUi.Publishable(false)
@ViewBinding(value = ImageViewModel.class)
public class Image extends Content implements Directory.Item {

    public static final String ICON_NAME = "photo";

    @MimeTypes("+image/")
    private StorageItem file;

    @ToolUi.CssClass("is-half")
    private String name;

    @ToolUi.CssClass("is-half")
    private String altText;

    @ToolUi.CssClass("is-half")
    private String caption;

    @ToolUi.CssClass("is-half")
    private String credit;

    private Link link;

    public StorageItem getFile() {
        return file;
    }

    public void setFile(StorageItem file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    // -- Overrides -- //

    @Override
    public String getLabel() {
        return getName();
    }

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        return null;
    }

    // -- Helper Methods -- //

    public Map<String, Object> getImageMetadata() {
        return Optional.ofNullable(getFile())
            .map(StorageItem::getMetadata)
            .orElse(new HashMap<>());
    }
}
