package com.brightspot.model.attachment;

import com.brightspot.model.link.Linkable;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.util.StorageItem;

@ToolUi.Publishable(false)
public class Attachment extends Content implements Linkable {

    private String name;

    @Required
    private StorageItem file;

    private Boolean autoDownload;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StorageItem getFile() {
        return file;
    }

    public void setFile(StorageItem file) {
        this.file = file;
    }

    public Boolean isAutoDownload() {
        return Boolean.TRUE.equals(autoDownload);
    }

    public void setAutoDownload(Boolean autoDownload) {
        this.autoDownload = autoDownload;
    }

    // -- Overrides -- //

    @Override
    public String getLinkableText() {
        return getName();
    }

    @Override
    public String getLinkableUrl(Site site) {
        return getUrl();
    }

    // -- Helper Methods -- //

    public String getUrl() {
        return String.join("/", AttachmentFilter.PATH, getId().toString());
    }
}
