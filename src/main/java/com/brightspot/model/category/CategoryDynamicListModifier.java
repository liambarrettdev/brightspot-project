package com.brightspot.model.category;

import java.util.HashSet;
import java.util.Set;

import com.brightspot.model.list.DynamicListModifiable;
import com.brightspot.model.list.DynamicListModifier;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.collections4.CollectionUtils;

public class CategoryDynamicListModifier extends Modification<DynamicListModifiable> implements DynamicListModifier {

    @ToolUi.Heading("Category")
    private Set<Category> categories;

    public Set<Category> getCategories() {
        if (categories == null) {
            categories = new HashSet<>();
        }
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public void updateQuery(Query<?> query) {
        if (ObjectUtils.isBlank(query) || CollectionUtils.isEmpty(getCategories())) {
            return;
        }
        query.where(HasCategory.Data.CATEGORY_FILED_INDEX + " = ?", getCategories());
    }
}
