package com.brightspot.tool;

import java.io.StringWriter;
import java.util.Optional;

import com.brightspot.model.image.Image;
import com.psddev.cms.db.ImageTag;
import com.psddev.dari.util.HtmlWriter;
import com.psddev.dari.util.StorageItem;

public interface HasImagePreview {

    default String writePreviewImageHtml(Image image) {
        StorageItem file = Optional.ofNullable(image)
            .map(Image::getFile)
            .orElse(null);

        if (file == null) {
            return "<span></span>";
        }

        StringWriter stringWriter = new StringWriter();
        HtmlWriter htmlWriter = new HtmlWriter(stringWriter);

        try {
            htmlWriter.writeStart("p", "style", "margin:0 0 3px 0;");
            {
                htmlWriter.writeHtml("Defaults to");
            }
            htmlWriter.writeEnd();

            htmlWriter.writeTag("img",
                "src", new ImageTag.Builder(file).setHeight(100).toUrl(),
                "style", "width: auto; height: 100px; border:solid 1px #cdcdcd; padding: 3px;"
            );
        } catch (Exception e) {
            // ignore
        }

        return stringWriter.toString();
    }
}
