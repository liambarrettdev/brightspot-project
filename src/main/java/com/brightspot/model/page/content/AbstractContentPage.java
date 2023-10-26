package com.brightspot.model.page.content;

import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.image.Image;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.slug.Sluggable;
import com.brightspot.model.tag.Taggable;
import com.brightspot.tool.HasImagePreview;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StringUtils;

@ViewBinding(value = AbstractContentPageViewModel.class, types = { PageFilter.PAGE_VIEW_TYPE })
public abstract class AbstractContentPage extends AbstractPage implements
    HasImagePreview,
    Hierarchical,
    Promotable,
    Sluggable,
    Taggable {

    @ToolUi.NoteHtml("<span data-dynamic-html='${content.getLeadImagePlaceholderHtml()}'></span>")
    private Image leadImage;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String headline;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
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
        return null;
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
    public Image getPromoImageFallback() {
        return getLeadImage();
    }

    // Sluggable

    public String getSlugFallback() {
        return StringUtils.toNormalized(getLabel());
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
