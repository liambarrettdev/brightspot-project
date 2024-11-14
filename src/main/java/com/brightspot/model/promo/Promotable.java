package com.brightspot.model.promo;

import java.util.Date;
import java.util.Optional;

import com.brightspot.model.image.Image;
import com.brightspot.model.link.Linkable;
import com.brightspot.tool.HasImagePreview;
import com.brightspot.utils.DirectoryUtils;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Modification;
import com.psddev.dari.util.ObjectUtils;

@ViewBinding(value = PromotableDelegateViewModel.class)
public interface Promotable extends Linkable {

    String INTERNAL_NAME = "com.brightspot.model.promo.Promotable";
    String DEFAULT_DATE_FORMAT = "MMMM d, yyyy";

    String getPromotableType();

    default Data asPromotableData() {
        return this.as(Data.class);
    }

    // defaults

    default String getPromoTitle() {
        return asPromotableData().getPromoTitle();
    }

    default String getPromoDescription() {
        return asPromotableData().getPromoDescription();
    }

    default Image getPromoImage() {
        return asPromotableData().getPromoImage();
    }

    default Date getPromotableDate() {
        return Optional.ofNullable(this.as(Content.ObjectModification.class))
            .map(Content.ObjectModification::getPublishDate)
            .orElse(null);
    }

    default String getPromotableDateFormat() {
        return DEFAULT_DATE_FORMAT;
    }

    default String getPromotableAuthor() {
        return null;
    }

    default String getPromotableDuration(Site site) {
        return null;
    }

    default String getPromotableUrl(Site site) {
        return DirectoryUtils.getCanonicalUrl(site, this);
    }

    // fallbacks

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
        public static final String EXCLUDE_FIELD = FIELD_PREFIX + "excludeFromDynamicResults";

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

        @Indexed
        @ToolUi.Tab(TAB_NAME)
        @ToolUi.Heading("Advanced")
        private Boolean excludeFromDynamicResults;

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

        public Boolean isExcludeFromDynamicResults() {
            return Boolean.TRUE.equals(excludeFromDynamicResults);
        }

        public void setExcludeFromDynamicResults(Boolean excludeFromDynamicResults) {
            this.excludeFromDynamicResults = excludeFromDynamicResults;
        }

        // -- Helper Methods -- //

        public String getPromoImagePlaceholderHtml() {
            return ObjectUtils.isBlank(promoImage)
                ? writePreviewImageHtml(getOriginalObject().getPromoImageFallback())
                : null;
        }
    }
}
