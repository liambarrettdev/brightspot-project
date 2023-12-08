package com.brightspot.model.list.sort.views;

import com.brightspot.model.list.sort.DynamicListSort;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
@Recordable.DisplayName("Page Views")
public class ViewsDynamicLostSort extends Record implements DynamicListSort {

    @Required
    private Direction direction = Direction.DESCENDING;

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    // -- Overrides -- //

    @Override
    public void updateQuery(Query<?> query) {
        getDirection().updateQuery(query);
    }

    // -- Enums -- //

    public enum Direction {
        ASCENDING("Least") {
            @Override
            public void updateQuery(Query<?> query) {
                query.sortAscending(ViewsSortable.Data.SORT_FIELD_INDEX);
            }
        },
        DESCENDING("Most") {
            @Override
            public void updateQuery(Query<?> query) {
                query.sortDescending(ViewsSortable.Data.SORT_FIELD_INDEX);
            }
        };

        private final String name;

        Direction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void updateQuery(Query<?> query) {
            // override in each enum
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
