package com.brightspot.model.tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brightspot.model.link.Linkable;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.PageViewModel;
import com.brightspot.model.slug.HasSlug;
import com.brightspot.utils.StateUtils;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Content;
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
@ViewBinding(value = TagPageViewModel.class, types = PageViewModel.MAIN_CONTENT_VIEW)
public class Tag extends AbstractPage implements
    HasSlug,
    Linkable,
    Taxon {

    @ToolUi.Note("This will override the default page content")
    private List<AbstractModule> contents;

    @Indexed
    @Where("_id != ?")
    @ToolUi.Filterable
    private Tag parent;

    public List<AbstractModule> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return contents;
    }

    public void setContents(List<AbstractModule> contents) {
        this.contents = contents;
    }

    public Tag getParent() {
        return parent;
    }

    public void setParent(Tag parent) {
        this.parent = parent;
    }

    // -- Overrides -- //

    @Override
    public String getLabel() {
        return isRoot()
            ? getName()
            : getAncestry().stream().map(StateUtils::resolve).map(Tag::getName).collect(Collectors.joining(" :: "));
    }

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        if (StringUtils.isBlank(asSlugData().getSlug())) {
            return null;
        }

        return Optional.ofNullable(getParent())
            .map(parent -> parent.createPermalink(site))
            .map(prefix -> StringUtils.appendIfMissing(prefix, "/"))
            .map(prefix -> prefix + asSlugData().getSlug())
            .orElse(asSlugData().getSlug());
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
    public boolean isRoot() {
        return ObjectUtils.isBlank(parent);
    }

    @Override
    public Collection<? extends Taxon> getChildren() {
        return Query.from(Tag.class)
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

    public List<Tag> getAncestry() {
        List<Tag> ancestry = new ArrayList<>(Collections.singletonList(this));

        if (!isRoot()) {
            Tag currentAncestor = getParent();
            while (currentAncestor != null && !currentAncestor.equals(this) && !ancestry.contains(currentAncestor)) {
                ancestry.add(currentAncestor);
                currentAncestor = currentAncestor.getParent();
            }
            Collections.reverse(ancestry);
        }

        return ancestry;
    }

    public List<HasTag> getMostRecentContent() {
        return Query.from(HasTag.class)
            .where(HasTag.Data.TAGS_FIELD + " = ?", getTaxonAndChildren())
            .sortDescending(Content.PUBLISH_DATE_FIELD)
            .resolveToReferenceOnly()
            .select(0, 10)
            .getItems();
    }
}
