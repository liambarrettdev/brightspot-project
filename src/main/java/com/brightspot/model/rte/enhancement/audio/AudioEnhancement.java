package com.brightspot.model.rte.enhancement.audio;

import java.io.IOException;

import com.brightspot.model.audio.Audio;
import com.brightspot.model.rte.enhancement.AbstractRichTextEnhancement;
import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.tool.rte.RichTextProcessor;
import com.psddev.cms.db.RichTextElement;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName(AudioEnhancement.ELEMENT_NAME)
@RichTextElement.Tag(value = AudioEnhancement.TAG_NAME,
    menu = AbstractRichTextEnhancement.MENU_GROUP,
    block = true,
    initialBody = AudioEnhancement.ELEMENT_NAME,
    position = -50.0,
    preview = true,
    readOnly = true,
    root = true,
    keymaps = { "Ctrl-Shift-a" },
    tooltip = "Add Audio"
)
@ToolUi.IconName(Audio.ICON_NAME)
@ViewBinding(value = AudioEnhancementViewModel.class, types = RichTextProcessor.RICH_TEXT_ELEMENT_VIEW_TYPE)
public class AudioEnhancement extends AbstractRichTextEnhancement implements Alignable {

    public static final String ELEMENT_NAME = "Audio";
    public static final String TAG_NAME = "bsp.audio";

    @Required
    private Audio audio;

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    // -- Overrides -- //

    @Override
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public void writePreviewHtml(ToolPageContext page) throws IOException {
        if (getAudio() != null) {
            String imageUrl = page.getPreviewThumbnailUrl(getAudio());
            if (imageUrl != null) {
                page.writeElement("img", "src", imageUrl, "height", 100);
            }
        }
    }
}
