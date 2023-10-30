package com.brightspot.model.category;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface Categorizable extends Recordable {

    default Categorizable.Data asCategorizableData() {
        return this.as(Categorizable.Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Categorizable.Data.FIELD_PREFIX)
    class Data extends Modification<Categorizable> {

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
