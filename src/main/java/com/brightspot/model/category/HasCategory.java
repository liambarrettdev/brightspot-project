package com.brightspot.model.category;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface HasCategory extends Recordable {

    default HasCategory.Data asCategorizableData() {
        return this.as(HasCategory.Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(HasCategory.Data.FIELD_PREFIX)
    class Data extends Modification<HasCategory> {

        public static final String FIELD_PREFIX = "categorizable.";
        public static final String CATEGORY_FIELD = FIELD_PREFIX + "category";

        @ToolUi.Heading("Category")

        @Indexed
        @ToolUi.Filterable
        private Category category;

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }
    }
}
