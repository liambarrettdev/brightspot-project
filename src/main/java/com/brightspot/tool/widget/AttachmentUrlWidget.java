package com.brightspot.tool.widget;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.brightspot.model.attachment.Attachment;
import com.psddev.cms.tool.CmsTool;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.tool.Widget;
import com.psddev.dari.db.State;

public class AttachmentUrlWidget extends Widget {

    public AttachmentUrlWidget() {
        setDisplayName("Attachment URL");
        setInternalName("attachmentUrlWidget");
        addPosition(CmsTool.CONTENT_RIGHT_WIDGET_POSITION, 0, 2);
    }

    @Override
    public boolean shouldDisplayInNonPublishable() {
        return true;
    }

    @Override
    public String createDisplayHtml(ToolPageContext page, Object object) throws Exception {
        if (!shouldProcess(object)) {
            return null;
        }

        Writer oldDelegate = page.getDelegate();
        StringWriter newDelegate = new StringWriter();

        try {
            page.setDelegate(newDelegate);
            writeDisplayHtml(page, (Attachment) object);
            return newDelegate.toString();
        } finally {
            page.setDelegate(oldDelegate);
        }
    }

    @Override
    public void update(ToolPageContext page, Object object) throws Exception {
        // do nothing
    }

    private static void writeDisplayHtml(ToolPageContext page, Attachment attachment) throws IOException {
        String url = attachment.getUrl();

        page.writeStart("div", "class", "widget");
        {
            if (url != null) {
                page.writeStart("h1").writeHtml(page.localize(AttachmentUrlWidget.class, "widget.title")).writeEnd();

                page.writeStart("a", "href", url, "target", "_blank");
                {
                    page.write(url);
                }
                page.writeEnd();
            }
        }
        page.writeEnd();
    }

    private static boolean shouldProcess(Object object) {
        State objState = State.getInstance(object);
        if (objState == null || objState.isNew()) {
            return false;
        }

        return object instanceof Attachment;
    }
}
