package com.brightspot.model.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.brightspot.model.category.Category;
import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.link.Linkable;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.taxonomy.Taxonomy;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.Taxon;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Query;

@ToolUi.FieldDisplayOrder({
    "name",
    "displayName",
    "contents",
    "categorizable.category"
})
@ViewBinding(value = PageMainViewModel.class, types = { PageViewModel.MAIN_CONTENT_VIEW })
public class Page extends AbstractPage implements
    Linkable,
    Taxonomy {

    private List<AbstractModule> contents;

    public List<AbstractModule> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return contents;
    }

    public void setContents(List<AbstractModule> contents) {
        this.contents = contents;
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
        return null;
    }

    // Linkable

    @Override
    public String getLinkableText() {
        return getDisplayName();
    }

    // Taxon

    @Override
    public Collection<? extends Taxon> getChildren() {
        return Query.from(Category.class)
            .where("* matches *")
            .and("parent = ?", this)
            .selectAll();
    }
}
