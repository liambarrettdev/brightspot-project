package com.brightspot.model.video.provider.html;

import java.util.Map;

import com.brightspot.tool.field.annotation.MimeTypes;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;

@Recordable.Embedded
public class HtmlVideoFileWrapper extends Record {

    @Required
    @MimeTypes("+video/")
    private StorageItem file;

    private Integer width;

    private Integer height;

    @ToolUi.Note("Time is seconds")
    private Long duration;

    public StorageItem getFile() {
        return file;
    }

    public void setFile(StorageItem file) {
        this.file = file;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
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
