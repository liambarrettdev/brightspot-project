package com.brightspot.model.rte.enhancement.gallery;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.brightspot.model.gallery.GalleryModule;
import com.brightspot.model.gallery.Slide;
import com.brightspot.model.rte.enhancement.AbstractRichTextEnhancement;
import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.tool.rte.RichTextProcessor;
import com.psddev.cms.db.RichTextElement;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName(GalleryEnhancement.ELEMENT_NAME)
@RichTextElement.Tag(value = GalleryEnhancement.TAG_NAME,
    menu = AbstractRichTextEnhancement.MENU_GROUP,
    block = true,
    initialBody = GalleryEnhancement.ELEMENT_NAME,
    position = -50.0,
    preview = true,
    readOnly = true,
    root = true,
    keymaps = { "Ctrl-Shift-g" },
    tooltip = "Add Gallery"
)
@ToolUi.IconName(GalleryEnhancement.ICON_NAME)
@ViewBinding(value = GalleryEnhancementViewModel.class, types = RichTextProcessor.RICH_TEXT_ELEMENT_VIEW_TYPE)
public class GalleryEnhancement extends AbstractRichTextEnhancement implements Alignable {

    public static final String ELEMENT_NAME = "Gallery";
    public static final String TAG_NAME = "bsp.gallery";
    public static final String ICON_NAME = "photo_library";

    private GalleryModule gallery;

    public GalleryModule getGallery() {
        return gallery;
    }

    public void setGallery(GalleryModule gallery) {
        this.gallery = gallery;
    }

    // -- Overrides -- //

    @Override
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public void writePreviewHtml(ToolPageContext page) throws IOException {
        if (getGallery() != null) {
            List<Slide> slides = getGallery().getSlides().stream()
                .limit(3)
                .collect(Collectors.toList());

            for (Slide slide : slides) {
                if (slide.getImage() != null) {
                    String imageUrl = page.getPreviewThumbnailUrl(slide.getImage());
                    if (imageUrl != null) {
                        page.writeElement("img", "src", imageUrl, "height", 100);
                    }
                }
            }
        }
    }
}
