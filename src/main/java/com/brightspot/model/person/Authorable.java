package com.brightspot.model.person;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface Authorable extends Recordable {

    default Data asAuthorableData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<Authorable> {

        public static final String FIELD_PREFIX = "authorable.";
        public static final String AUTHOR_FIELD = FIELD_PREFIX + "author";

        @Indexed
        @ToolUi.Filterable
        private Person author;

        public Person getAuthor() {
            return author;
        }

        public void setAuthor(Person author) {
            this.author = author;
        }
    }
}
