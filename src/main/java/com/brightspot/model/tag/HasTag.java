package com.brightspot.model.tag;

import java.util.ArrayList;
import java.util.List;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface HasTag extends Recordable {

    default Data asTagData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<HasTag> {

        public static final String FIELD_PREFIX = "taggable.";
        public static final String TAGS_FIELD = FIELD_PREFIX + "tags";

        @ToolUi.Heading("Tagging")

        @Recordable.Indexed
        @ToolUi.Filterable
        private List<Tag> tags;

        public List<Tag> getTags() {
            if (tags == null) {
                tags = new ArrayList<>();
            }
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }
}
