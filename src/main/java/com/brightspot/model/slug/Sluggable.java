package com.brightspot.model.slug;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

public interface Sluggable extends Recordable {

    String getSlugFallback();

    default Data asSluggableData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<Sluggable> {

        public static final String FIELD_PREFIX = "sluggable.";

        @ToolUi.Placeholder(dynamicText = "${content.getSlugFallback()}")
        private String slug;

        public String getSlug() {
            return ObjectUtils.firstNonBlank(slug, getOriginalObject().getSlugFallback());
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }
    }
}
