package com.brightspot.model.blog;

import java.util.Collection;
import java.util.Collections;

import com.brightspot.model.category.HasCategory;
import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.image.Image;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.AbstractPageViewModel;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.taxonomy.Taxonomy;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.brightspot.utils.RichTextUtils;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.Taxon;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;

@ViewBinding(value = BlogPageViewModel.class, types = AbstractPageViewModel.MAIN_CONTENT_VIEW)
public class Blog extends AbstractPage implements
    HasCategory,
    Promotable,
    Taxonomy {

    private Image leadImage;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String description;

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

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        return null;
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

    // Taxon

    @Override
    public Collection<? extends Taxon> getChildren() {
        return Collections.emptyList();
    }
}
