package com.brightspot.model.audio.provider.html;

import java.util.Map;

import com.brightspot.tool.field.annotation.MimeTypes;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;

@Recordable.Embedded
public class HtmlFileWrapper extends Record {

    @Required
    @MimeTypes("+audio/")
    private StorageItem file;

    private Long duration;

    public StorageItem getFile() {
        return file;
    }

    public void setFile(StorageItem file) {
        this.file = file;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getMimeType() {
        return file != null ? file.getContentType() : null;
    }

    @Override
    public String getLabel() {
        String label = null;

        if (file != null && file.getContentType() != null) {
            Map<String, Object> metadata = file.getMetadata();
            if (metadata != null) {
                label = ObjectUtils.to(String.class, metadata.get("originalFilename"));
            }
            if (label == null) {
                label = file.getContentType();
            }
        }

        return label;
    }
}
