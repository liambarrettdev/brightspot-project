package com.brightspot.model.rte.enhancement.video;

import java.io.IOException;

import com.brightspot.model.rte.enhancement.AbstractRichTextEnhancement;
import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.model.video.Video;
import com.brightspot.tool.rte.RichTextProcessor;
import com.psddev.cms.db.RichTextElement;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName(VideoEnhancement.ELEMENT_NAME)
@RichTextElement.Tag(value = VideoEnhancement.TAG_NAME,
    menu = AbstractRichTextEnhancement.MENU_GROUP,
    block = true,
    initialBody = VideoEnhancement.ELEMENT_NAME,
    position = -50.0,
    preview = true,
    readOnly = true,
    root = true,
    keymaps = { "Ctrl-Shift-v" },
    tooltip = "Add Video"
)
@ToolUi.IconName(Video.ICON_NAME)
@ViewBinding(value = VideoEnhancementViewModel.class, types = RichTextProcessor.RICH_TEXT_ELEMENT_VIEW_TYPE)
public class VideoEnhancement extends AbstractRichTextEnhancement implements Alignable {

    public static final String ELEMENT_NAME = "Video";
    public static final String TAG_NAME = "bsp.video";

    @Recordable.Required
    private Video video;

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    // -- Overrides -- //

    @Override
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public void writePreviewHtml(ToolPageContext page) throws IOException {
        if (getVideo() != null) {
            String imageUrl = page.getPreviewThumbnailUrl(getVideo());
            if (imageUrl != null) {
                page.writeElement("img", "src", imageUrl, "height", 100);
            }
        }
    }
}
