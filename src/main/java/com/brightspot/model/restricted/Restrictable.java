package com.brightspot.model.restricted;

import java.util.Optional;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface Restrictable extends Recordable {

    default Data asRestrictableData() {
        return this.as(Data.class);
    }

    static boolean isRestricted(Recordable content) {
        return Optional.ofNullable(content)
            .filter(Restrictable.class::isInstance)
            .map(Restrictable.class::cast)
            .map(Restrictable::asRestrictableData)
            .map(Data::isRestrictedContent)
            .orElse(false);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<Restrictable> {

        public static final String FIELD_PREFIX = "restrictable.";

        public static final String TAB_NAME = "Access";

        @Indexed
        @ToolUi.Tab(TAB_NAME)
        @ToolUi.Note("Page will be restricted to logged in users only")
        private Boolean restrictedContent;

        public Boolean isRestrictedContent() {
            return Boolean.TRUE.equals(restrictedContent);
        }

        public void setRestrictedContent(Boolean restrictedContent) {
            this.restrictedContent = restrictedContent;
        }
    }
}
