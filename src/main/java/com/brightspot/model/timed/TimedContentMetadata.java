package com.brightspot.model.timed;

import java.util.Optional;

import com.brightspot.utils.Utils;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.StringUtils;

public interface TimedContentMetadata extends Recordable {

    Long getDuration();

    default Data asTimedContentData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<TimedContentMetadata> {

        public static final String FIELD_PREFIX = "timed.";

        public String getDurationString() {
            return Optional.ofNullable(getOriginalObject().getDuration())
                .map(Utils::convertTime)
                .orElse(null);
        }

        public String getDurationLabel() {
            if (StringUtils.isBlank(getDurationString())) {
                return null;
            }
            return "Length: " + getDurationString();
        }
    }
}
