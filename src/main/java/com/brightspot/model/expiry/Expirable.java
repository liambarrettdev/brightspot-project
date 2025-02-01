package com.brightspot.model.expiry;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface Expirable extends Recordable {

    Boolean isExpired();

    default Data asExpirableData() {
        return this.as(Data.class);
    }

    // defaults

    default Boolean shouldDeleteAfterExpiry() {
        return false;
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<Expirable> {

        public static final String FIELD_PREFIX = "expirable.";
        public static final String EXPIRED_FIELD = FIELD_PREFIX + "expired";
        public static final String EXPIRED_PREDICATE = String.format("%s = true", EXPIRED_FIELD);
        public static final String NOT_EXPIRED_PREDICATE = String.format("(%1$s = missing or %1$s = false)", EXPIRED_FIELD);

        @Indexed(visibility = true)
        @ToolUi.ReadOnly
        private Boolean expired;

        public boolean isExpired() {
            return Boolean.TRUE.equals(expired);
        }

        public void setExpired(boolean expired) {
            this.expired = expired ? Boolean.TRUE : null;
        }

        @Override
        public void beforeCommit() {
            this.setExpired(Boolean.TRUE.equals(getOriginalObject().isExpired()));

            super.beforeCommit();
        }
    }
}
