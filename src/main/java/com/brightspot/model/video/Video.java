package com.brightspot.model.video;

import java.util.Optional;

import com.brightspot.model.image.Image;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Directory;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.StorageItem;

@Content.PreviewField("getPreviewStorageItem")
@ToolUi.IconName(Video.ICON_NAME)
@ToolUi.Publishable(false)
@ViewBinding(value = VideoViewModel.class)
public class Video extends Content implements Directory.Item {

    public static final String ICON_NAME = "slideshow";

    private String name;

    @Ignored(false)
    @ToolUi.Hidden
    public StorageItem getPreviewStorageItem() {
        return Optional.ofNullable(getThumbnail())
            .map(Image::getFile)
            .orElse(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Image getThumbnail() {
        return null;
    }

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        return null;
    }
}
