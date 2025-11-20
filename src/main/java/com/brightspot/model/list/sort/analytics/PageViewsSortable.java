package com.brightspot.model.list.sort.analytics;

import java.util.Optional;

import com.brightspot.utils.AnalyticsUtils;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.State;

public interface PageViewsSortable extends Recordable {

    default Long getPageViews() {
        return AnalyticsUtils.getPageViews(getState().getId(), null, null);
    }

    static void recalculateValues(State state) {
        Optional.ofNullable(ObjectType.getInstance(Data.class))
            .map(o -> o.getMethod(Data.SORT_FIELD))
            .ifPresent(m -> m.recalculate(state));
    }

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<PageViewsSortable> {

        public static final String FIELD_PREFIX = "views.";
        public static final String SORT_FIELD = FIELD_PREFIX + "getSortValue";
        public static final String SORT_FIELD_INDEX = PageViewsSortable.class.getName() + "/" + SORT_FIELD;

        @Recordable.Indexed
        @ToolUi.Hidden
        @ToolUi.Sortable
        public Long getSortValue() {
            return getOriginalObject().getPageViews();
        }
    }
}
