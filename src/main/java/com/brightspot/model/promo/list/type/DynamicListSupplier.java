package com.brightspot.model.promo.list.type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.brightspot.model.list.DynamicListModifiable;
import com.brightspot.model.list.DynamicListModifier;
import com.brightspot.model.list.ListSupplier;
import com.brightspot.model.promo.Promotable;
import com.psddev.cms.db.Directory;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.DisplayName("Dynamic")
public class DynamicListSupplier extends ListSupplier implements DynamicListModifiable {

    private static final String DEFAULT_ITEMS_PER_PAGE = "10";
    private static final String PROMOTABLE_PREDICATE = "groups = " + Promotable.INTERNAL_NAME
        + " && internalName != " + Promotable.INTERNAL_NAME
        + " && (cms.ui.hidden = false || cms.ui.hidden = missing)"
        + " && isAbstract = false";

    @Where(PROMOTABLE_PREDICATE)
    @ToolUi.DropDown
    private Set<ObjectType> types;

    @ToolUi.Placeholder(DEFAULT_ITEMS_PER_PAGE)
    private Integer itemsPerPage = Integer.parseInt(DEFAULT_ITEMS_PER_PAGE);

    public Set<ObjectType> getTypes() {
        if (types == null) {
            types = new HashSet<>();
        }
        return types;
    }

    public void setTypes(Set<ObjectType> types) {
        this.types = types;
    }

    public Integer getItemsPerPage() {
        return ObjectUtils.firstNonBlank(itemsPerPage, Integer.parseInt(DEFAULT_ITEMS_PER_PAGE));
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    @Override
    public List<Promotable> getItems(Site site) {
        // query from solr
        Query<Promotable> query = Query.from(Promotable.class).where("* matches *");

        // only results from current site
        if (site != null) {
            query.and(site.itemsPredicate());
        }

        // only selected content types
        if (!getTypes().isEmpty()) {
            query.where("_type = ?", getTypes());
        }

        // only content with a permalink
        query.and(Directory.Static.hasPathPredicate());

        // omit excluded content
        query.and(Promotable.Data.EXCLUDE_FIELD + " != true");

        // apply extra query modifiers
        DynamicListModifier.updateQueryWithModifications(this, query);

        //TODO add sorting

        return query.selectAll();
    }
}
