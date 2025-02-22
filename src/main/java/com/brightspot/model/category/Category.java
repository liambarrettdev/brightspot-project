package com.brightspot.model.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.image.Image;
import com.brightspot.model.link.Linkable;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.Page;
import com.brightspot.model.page.PageViewModel;
import com.brightspot.model.slug.HasSlug;
import com.brightspot.model.taxonomy.Taxonomy;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Directory;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.Taxon;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@ToolUi.DefaultSortField("displayName")
@ToolUi.FieldDisplayOrder({
    "name",
    "displayName",
    "sluggable.slug",
    "contents",
    "parent"
})
@ViewBinding(value = CategoryPageViewModel.class, types = PageViewModel.MAIN_CONTENT_VIEW)
public class Category extends AbstractPage implements
    HasSlug,
    Linkable,
    Taxonomy {

    private Image image;

    @ToolUi.Note("This will override the default page content")
    private List<AbstractModule> contents;

    @Indexed
    @Where("_id != ?")
    @Types({ Category.class, Page.class })
    @ToolUi.Filterable
    private Hierarchical parent;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<AbstractModule> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return contents;
    }

    public void setContents(List<AbstractModule> contents) {
        this.contents = contents;
    }

    public Hierarchical getParent() {
        return parent;
    }

    public void setParent(Hierarchical parent) {
        this.parent = parent;
    }

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        if (StringUtils.isBlank(asSlugData().getSlug())) {
            return null;
        }

        return Optional.ofNullable(getHierarchicalParent())
            .filter(Directory.Item.class::isInstance)
            .map(Directory.Item.class::cast)
            .map(parent -> parent.createPermalink(site))
            .map(prefix -> StringUtils.appendIfMissing(prefix, "/"))
            .map(prefix -> prefix + asSlugData().getSlug())
            .orElse(asSlugData().getSlug());
    }

    // Hierarchical

    @Override
    public Hierarchical getHierarchicalParent() {
        return getParent();
    }

    // Linkable

    @Override
    public String getLinkableText() {
        return getDisplayName();
    }

    // Sluggable

    @Override
    public String getSlugFallback() {
        return Utils.toNormalized(getDisplayName());
    }

    // Taxon

    @Override
    public Collection<? extends Taxon> getChildren() {
        return Query.from(Category.class)
            .where("* matches *")
            .and("parent = ?", this)
            .resolveToReferenceOnly()
            .selectAll();
    }

    // -- Helper Methods -- //

    public List<Taxon> getTaxonAndChildren() {
        ArrayList<Taxon> items = new ArrayList<>(Collections.singletonList(this));
        items.addAll(getChildren());
        return items;
    }

    public List<Hierarchical> getAncestry() {
        List<Hierarchical> ancestry = new ArrayList<>(Collections.singletonList(this));

        Hierarchical currentAncestor = getParent();
        if (!ObjectUtils.isBlank(currentAncestor)) {
            while (currentAncestor != null && !currentAncestor.equals(this) && !ancestry.contains(currentAncestor)) {
                ancestry.add(currentAncestor);
                currentAncestor = currentAncestor.getHierarchicalParent();
            }
            Collections.reverse(ancestry);
        }

        return ancestry;
    }

    public List<HasCategory> getMostRecentContent() {
        return Query.from(HasCategory.class)
            .where(HasCategory.Data.CATEGORY_FIELD + " = ?", getTaxonAndChildren())
            .sortDescending(Content.PUBLISH_DATE_FIELD)
            .resolveToReferenceOnly()
            .select(0, 10)
            .getItems();
    }

}
