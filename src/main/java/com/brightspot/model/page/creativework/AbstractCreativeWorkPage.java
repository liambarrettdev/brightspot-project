package com.brightspot.model.page.creativework;

import java.util.Optional;

import com.brightspot.model.category.HasCategory;
import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.image.Image;
import com.brightspot.model.list.sort.alphabetical.AlphabeticalSortable;
import com.brightspot.model.list.sort.analytics.PageViewsSortable;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.person.HasAuthor;
import com.brightspot.model.person.Person;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.slug.HasSlug;
import com.brightspot.model.tag.HasTag;
import com.brightspot.tool.HasImagePreview;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Seo;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.util.ObjectUtils;

@Seo.DescriptionFields("subHeadline")
public abstract class AbstractCreativeWorkPage extends AbstractPage implements
    AlphabeticalSortable,
    HasAuthor,
    HasCategory,
    HasImagePreview,
    HasSlug,
    HasTag,
    Hierarchical,
    PageViewsSortable,
    Promotable {

    public static final String INTERNAL_NAME = "com.brightspot.model.page.creativework.AbstractCreativeWorkPage";

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
        return asSlugData().getSlug();
    }

    // Hierarchical

    @Override
    public Hierarchical getHierarchicalParent() {
        return asCategoryData().getCategory();
    }

    // Linkable

    @Override
    public String getLinkableText() {
        return getDisplayName();
    }

    // Promotable

    @Override
    public String getPromotableAuthor() {
        return Optional.ofNullable(asAuthorData().getAuthor())
            .map(Person::getDisplayName)
            .orElse(null);
    }

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
