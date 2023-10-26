package com.brightspot.model.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.slug.Sluggable;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.Taxon;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StringUtils;

@ToolUi.DefaultSortField("displayName")
@ToolUi.FieldDisplayOrder({
    "name",
    "displayName",
    "sluggable.slug",
    "parent"
})
@ViewBinding(value = TagViewModel.class)
public class Tag extends AbstractPage implements
    Sluggable,
    Taxon {

    @Indexed
    @ToolUi.Filterable
    private Tag parent;

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
            : getAncestry().stream().map(Tag::getName).collect(Collectors.joining(" :: "));
    }

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        if (StringUtils.isBlank(asSluggableData().getSlug())) {
            return null;
        }

        return Optional.ofNullable(getParent())
            .map(parent -> parent.createPermalink(site))
            .map(prefix -> StringUtils.ensureEnd(prefix, "/"))
            .map(prefix -> prefix + asSluggableData().getSlug())
            .orElse(asSluggableData().getSlug());
    }

    // Sluggable

    @Override
    public String getSlugFallback() {
        return StringUtils.toNormalized(getDisplayName());
    }

    // Taxon

    @Override
    public boolean isRoot() {
        return ObjectUtils.isBlank(parent);
    }

    @Override
    public List<Tag> getChildren() {
        return Query.from(Tag.class)
            .where("* matches *")
            .and("parent = ?", this)
            .selectAll();
    }

    // -- Helper Methods -- //

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
}
