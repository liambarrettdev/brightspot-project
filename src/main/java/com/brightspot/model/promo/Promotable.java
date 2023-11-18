package com.brightspot.model.promo;

import com.brightspot.model.image.Image;
import com.brightspot.model.link.Linkable;
import com.brightspot.tool.HasImagePreview;
import com.brightspot.utils.DirectoryUtils;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Modification;
import com.psddev.dari.util.ObjectUtils;

@ViewBinding(value = PromotableViewModel.class)
public interface Promotable extends Linkable {

    default Data asPromotableData() {
        return this.as(Data.class);
    }

    default String getPromoTitle() {
        return asPromotableData().getPromoTitle();
    }

    default String getPromoDescription() {
        return asPromotableData().getPromoDescription();
    }

    default Image getPromoImage() {
        return asPromotableData().getPromoImage();
    }

    default String getPromotableUrl(Site site) {
        return DirectoryUtils.getCanonicalUrl(site, this);
    }

    default String getPromoTitleFallback() {
        return null;
    }

    default String getPromoDescriptionFallback() {
        return null;
    }

    default Image getPromoImageFallback() {
        return null;
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<Promotable> implements HasImagePreview {

        public static final String FIELD_PREFIX = "promotable.";

        public static final String TAB_NAME = "Promo";

        @ToolUi.Tab(TAB_NAME)
        @ToolUi.Placeholder(dynamicText = "${content.getPromoTitleFallback()}", editable = true)
        private String promoTitle;

        @ToolUi.Tab(TAB_NAME)
        @ToolUi.Placeholder(dynamicText = "${content.getPromoDescriptionFallback()}", editable = true)
        private String promoDescription;

        @ToolUi.Tab(TAB_NAME)
        @ToolUi.NoteHtml("<span data-dynamic-html='${content.asPromotableData().getPromoImagePlaceholderHtml()}'></span>")
        private Image promoImage;

        public String getPromoTitle() {
            return ObjectUtils.firstNonBlank(promoTitle, getOriginalObject().getPromoTitleFallback());
        }

        public void setPromoTitle(String promoTitle) {
            this.promoTitle = promoTitle;
        }

        public String getPromoDescription() {
            return ObjectUtils.firstNonBlank(promoDescription, getOriginalObject().getPromoDescriptionFallback());
        }

        public void setPromoDescription(String promoDescription) {
            this.promoDescription = promoDescription;
        }

        public Image getPromoImage() {
            return ObjectUtils.firstNonBlank(promoImage, getOriginalObject().getPromoImageFallback());
        }

        public void setPromoImage(Image promoImage) {
            this.promoImage = promoImage;
        }

        // -- Helper Methods -- //

        public String getPromoImagePlaceholderHtml() {
            return ObjectUtils.isBlank(promoImage)
                ? writePreviewImageHtml(getOriginalObject().getPromoImageFallback())
                : null;
        }
    }
}
