package com.brightspot.model.blog;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.brightspot.model.category.HasCategory;
import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.image.Image;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.PageMainViewModel;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.slug.Sluggable;
import com.brightspot.model.taxonomy.Taxonomy;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.brightspot.utils.RichTextUtils;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Directory;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.Taxon;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@ToolUi.FieldDisplayOrder({
    "name",
    "displayName",
    "sluggable.slug",
    "leadImage",
    "description",
    "postsPerPage",
    "categorizable.category"
})
@ViewBinding(value = BlogPageViewModel.class, types = PageMainViewModel.MAIN_CONTENT_VIEW)
public class Blog extends AbstractPage implements
    HasCategory,
    Promotable,
    Sluggable,
    Taxonomy {

    private static final String PROMOTABLE_TYPE = "blog";

    private Image leadImage;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String description;

    private Integer postsPerPage;

    public Image getLeadImage() {
        return leadImage;
    }

    public void setLeadImage(Image leadImage) {
        this.leadImage = leadImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPostsPerPage() {
        return ObjectUtils.firstNonBlank(postsPerPage, 0);
    }

    public void setPostsPerPage(Integer postsPerPage) {
        this.postsPerPage = postsPerPage;
    }

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        if (StringUtils.isBlank(asSluggableData().getSlug())) {
            return null;
        }

        return Optional.ofNullable(getHierarchicalParent())
            .filter(Directory.Item.class::isInstance)
            .map(Directory.Item.class::cast)
            .map(parent -> parent.createPermalink(site))
            .map(prefix -> StringUtils.appendIfMissing(prefix, "/"))
            .map(prefix -> prefix + asSluggableData().getSlug())
            .orElse(asSluggableData().getSlug());
    }

    // Hierarchical

    @Override
    public Hierarchical getHierarchicalParent() {
        return asCategorizableData().getCategory();
    }

    // Linkable

    @Override
    public String getLinkableText() {
        return getPromoTitle();
    }

    // Promotable

    @Override
    public String getPromoTitleFallback() {
        return getDisplayName();
    }

    @Override
    public String getPromoDescriptionFallback() {
        return RichTextUtils.richTextToPlainText(getDescription());
    }

    @Override
    public String getPromotableType() {
        return PROMOTABLE_TYPE;
    }

    // Sluggable

    @Override
    public String getSlugFallback() {
        return Utils.toNormalized(getDisplayName());
    }

    // Taxon

    @Override
    public Collection<? extends Taxon> getChildren() {
        return Collections.emptyList();
    }
}
