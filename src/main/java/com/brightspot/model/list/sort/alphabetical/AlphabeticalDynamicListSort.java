package com.brightspot.model.list.sort.alphabetical;

import com.brightspot.model.list.sort.DynamicListSort;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
@Recordable.DisplayName("Alphabetical")
public class AlphabeticalDynamicListSort extends Record implements DynamicListSort {

    @Required
    private Direction direction = Direction.ASCENDING;

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
        ASCENDING("A-Z") {
            @Override
            public void updateQuery(Query<?> query) {
                query.sortAscending(AlphabeticalSortable.Data.SORT_FIELD_INDEX);
            }
        },
        DESCENDING("Z-A") {
            @Override
            public void updateQuery(Query<?> query) {
                query.sortDescending(AlphabeticalSortable.Data.SORT_FIELD_INDEX);
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
