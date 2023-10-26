package com.brightspot.model.rte.enhancement;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface Alignable extends Recordable {

    default Data asAlignableData() {
        return this.as(Data.class);
    }

    // -- Modification Data --//

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<Alignable> {

        public static final String FIELD_PREFIX = "alignable.";

        @ToolUi.Heading("Positioning")

        private Alignment alignment;

        public Alignment getAlignment() {
            return alignment;
        }

        public void setAlignment(Alignment alignment) {
            this.alignment = alignment;
        }

        // -- Enums -- //

        public enum Alignment {
            CENTER("Center"),
            LEFT("Left"),
            RIGHT("Right"),
            FULL("Full Width");

            private final String name;

            Alignment(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            @Override
            public String toString() {
                return getName();
            }
        }
    }
}
