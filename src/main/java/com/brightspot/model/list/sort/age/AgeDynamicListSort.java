package com.brightspot.model.list.sort.age;

import com.brightspot.model.list.sort.DynamicListSort;
import com.psddev.cms.db.Content;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
@Recordable.DisplayName("Age")
public class AgeDynamicListSort extends Record implements DynamicListSort {

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
        ASCENDING("Oldest") {
            @Override
            public void updateQuery(Query<?> query) {
                query.sortAscending(Content.PUBLISH_DATE_FIELD);
            }
        },
        DESCENDING("Newest") {
            @Override
            public void updateQuery(Query<?> query) {
                query.sortDescending(Content.PUBLISH_DATE_FIELD);
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
