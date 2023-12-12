package com.brightspot.model.category;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface HasCategory extends Recordable {

    default Data asCategoryData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<HasCategory> {

        public static final String FIELD_PREFIX = "categorizable.";
        public static final String CATEGORY_FIELD = FIELD_PREFIX + "category";
        public static final String CATEGORY_FILED_INDEX = Data.class.getName() + "/" + CATEGORY_FIELD;

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
