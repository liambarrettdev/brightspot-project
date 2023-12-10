package com.brightspot.model.list.sort.analytics;

import com.brightspot.utils.AnalyticsUtils;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.State;

public interface PageViewsSortable extends Recordable {

    static void recalculateValues(State state) {
        ObjectType type = ObjectType.getInstance(Data.class);

        type.getMethod(Data.SORT_FIELD).recalculate(state);
    }

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<PageViewsSortable> {

        public static final String FIELD_PREFIX = "views.";
        public static final String SORT_FIELD = FIELD_PREFIX + "getSortValue";
        public static final String SORT_FIELD_INDEX = PageViewsSortable.class.getName() + "/" + SORT_FIELD;

        @Indexed
        @ToolUi.Hidden
        @ToolUi.Sortable
        public Long getSortValue() {
            return AnalyticsUtils.getPageViews(getId(), null, null);
        }
    }
}
