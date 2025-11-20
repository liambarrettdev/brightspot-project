package com.brightspot.model.list.sort.alphabetical;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface AlphabeticalSortable extends Recordable {

    default String getAlphabeticalSortValue() {
        return getState().getLabel();
    }

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<AlphabeticalSortable> {

        public static final String FIELD_PREFIX = "alphabetical.";
        public static final String SORT_FIELD = FIELD_PREFIX + "getSortValue";
        public static final String SORT_FIELD_INDEX = AlphabeticalSortable.class.getName() + "/" + SORT_FIELD;

        @Recordable.Indexed
        @ToolUi.Hidden
        public String getSortValue() {
            return getOriginalObject().getAlphabeticalSortValue();
        }
    }
}
