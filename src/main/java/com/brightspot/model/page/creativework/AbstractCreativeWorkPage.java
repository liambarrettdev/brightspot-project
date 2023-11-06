package com.brightspot.model.page.creativework;

import com.brightspot.model.category.Categorizable;
import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.image.Image;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.person.Authorable;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.slug.Sluggable;
import com.brightspot.model.tag.Taggable;
import com.brightspot.tool.HasImagePreview;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Seo;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.util.ObjectUtils;

@Seo.DescriptionFields("subHeadline")
public abstract class AbstractCreativeWorkPage extends AbstractPage implements
    Authorable,
    Categorizable,
    HasImagePreview,
    Hierarchical,
    Promotable,
    Sluggable,
    Taggable {

    @ToolUi.NoteHtml("<span data-dynamic-html='${content.getLeadImagePlaceholderHtml()}'></span>")
    private Image leadImage;

    private String headline;

    private String subHeadline;

    public Image getLeadImage() {
        return ObjectUtils.firstNonBlank(leadImage, getLeadImageFallback());
    }

    public void setLeadImage(Image leadImage) {
        this.leadImage = leadImage;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSubHeadline() {
        return subHeadline;
    }

    public void setSubHeadline(String subHeadline) {
        this.subHeadline = subHeadline;
    }

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        return asSluggableData().getSlug();
    }

    // Hierarchical

    @Override
    public Hierarchical getHierarchicalParent() {
        return asCategorizableData().getCategory();
    }

    // Linkable

    @Override
    public String getLinkableText() {
        return getDisplayName();
    }

    // Promotable

    @Override
    public String getPromoTitleFallback() {
        return getDisplayName();
    }

    @Override
    public String getPromoDescriptionFallback() {
        return getSubHeadline();
    }

    @Override
    public Image getPromoImageFallback() {
        return getLeadImage();
    }

    // Sluggable

    public String getSlugFallback() {
        return Utils.toNormalized(getLabel());
    }

    // -- Helper Methods -- //

    public Image getLeadImageFallback() {
        return null;
    }

    public String getLeadImagePlaceholderHtml() {
        return ObjectUtils.isBlank(leadImage)
            ? writePreviewImageHtml(getLeadImageFallback())
            : null;
    }
}
