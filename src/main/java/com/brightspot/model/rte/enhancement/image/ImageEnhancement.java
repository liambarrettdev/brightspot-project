package com.brightspot.model.rte.enhancement.image;

import java.io.IOException;

import com.brightspot.model.image.Image;
import com.brightspot.model.link.Link;
import com.brightspot.model.rte.enhancement.AbstractRichTextEnhancement;
import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.tool.rte.RichTextProcessor;
import com.psddev.cms.db.RichTextElement;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName(ImageEnhancement.ELEMENT_NAME)
@RichTextElement.Tag(value = ImageEnhancement.TAG_NAME,
    menu = AbstractRichTextEnhancement.MENU_GROUP,
    block = true,
    initialBody = ImageEnhancement.ELEMENT_NAME,
    position = -50.0,
    preview = true,
    readOnly = true,
    root = true,
    keymaps = { "Ctrl-Shift-i" },
    tooltip = "Add Image"
)
@ToolUi.IconName(Image.ICON_NAME)
@ViewBinding(value = ImageEnhancementViewModel.class, types = RichTextProcessor.RICH_TEXT_ELEMENT_VIEW_TYPE)
public class ImageEnhancement extends AbstractRichTextEnhancement implements Alignable {

    public static final String ELEMENT_NAME = "Image";
    public static final String TAG_NAME = "bsp.image";

    @Recordable.Required
    private Image image;

    private Link link;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    // -- Overrides -- //

    @Override
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public void writePreviewHtml(ToolPageContext page) throws IOException {
        if (getImage() != null) {
            String imageUrl = page.getPreviewThumbnailUrl(getImage());
            if (imageUrl != null) {
                page.writeElement("img", "src", imageUrl, "height", 100);
            }
        }
    }
}
