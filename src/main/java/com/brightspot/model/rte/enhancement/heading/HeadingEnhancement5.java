package com.brightspot.model.rte.enhancement.heading;

import com.psddev.cms.db.RichTextElement;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("H5")
@RichTextElement.Tag(value = HeadingEnhancement5.TAG_NAME,
    menu = "Headings",
    block = true,
    position = -60.0)
public class HeadingEnhancement5 extends AbstractHeadingEnhancement {

    public static final String TAG_NAME = "h5";

    // -- Overrides -- //

    @Override
    protected String getTag() {
        return TAG_NAME;
    }
}
