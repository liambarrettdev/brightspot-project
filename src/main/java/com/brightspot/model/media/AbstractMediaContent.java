package com.brightspot.model.media;

import java.util.Optional;

import com.brightspot.model.image.Image;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.util.StorageItem;

@ToolUi.Publishable(false)
@Content.PreviewField("getPreviewStorageItem")
public abstract class AbstractMediaContent extends Content {

    public abstract Image getPreviewImage();

    @Indexed
    @ToolUi.Hidden
    public StorageItem getPreviewStorageItem() {
        return Optional.ofNullable(getPreviewImage())
            .map(Image::getFile)
            .orElse(null);
    }
}
