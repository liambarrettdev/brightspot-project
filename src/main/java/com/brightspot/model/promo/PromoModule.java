package com.brightspot.model.promo;

import java.util.Optional;

import com.brightspot.model.image.Image;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.brightspot.tool.HasImagePreview;
import com.brightspot.utils.LocalizationUtils;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.DisplayName("Promo")
@ViewBinding(value = PromoModuleViewModel.class, types = PromoModule.VIEW_CLASS)
public class PromoModule extends AbstractModule implements
    HasImagePreview,
    ShareableModule {

    protected static final String VIEW_CLASS = "promo-module";

    @Required
    @Embedded
    private Promo promo = new NoLinkPromo();

    @ToolUi.Placeholder(dynamicText = "${content.getTitleFallback()}", editable = true)
    private String title;

    @ToolUi.Placeholder(dynamicText = "${content.getDescriptionFallback()}", editable = true)
    private String description;

    @ToolUi.NoteHtml("<span data-dynamic-html='${content.getPromoImagePlaceholderHtml()}'></span>")
    private Image image;

    @ToolUi.Placeholder(dynamicText = "${content.getButtonTextFallback()}", editable = true)
    @DisplayName("CTA Text")
    private String ctaText;

    public Promo getPromo() {
        return promo;
    }

    public void setPromo(Promo promo) {
        this.promo = promo;
    }

    public String getTitle() {
        return ObjectUtils.firstNonBlank(title, getTitleFallback());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return ObjectUtils.firstNonBlank(description, getDescriptionFallback());
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getImage() {
        return ObjectUtils.firstNonBlank(image, getImageFallback());
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getCtaText() {
        return ObjectUtils.firstNonBlank(ctaText, getButtonTextFallback());
    }

    public void setCtaText(String ctaText) {
        this.ctaText = ctaText;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_CLASS;
    }

    @Override
    public String getLabel() {
        return getTitle();
    }

    // -- Helper Methods -- //

    public String getTitleFallback() {
        return Optional.ofNullable(getPromo())
            .map(Promo::getTitle)
            .orElse(null);
    }

    public String getDescriptionFallback() {
        return Optional.ofNullable(getPromo())
            .map(Promo::getDescription)
            .orElse(null);
    }

    public Image getImageFallback() {
        return Optional.ofNullable(getPromo())
            .map(Promo::getImage)
            .orElse(null);
    }

    public String getButtonTextFallback() {
        return LocalizationUtils.currentSiteText(this, "ctaText");
    }

    public String getPromoImagePlaceholderHtml() {
        return ObjectUtils.isBlank(image)
            ? writePreviewImageHtml(getImageFallback())
            : null;
    }
}
