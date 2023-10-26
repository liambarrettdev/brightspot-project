package com.brightspot.model.rte.enhancement.heading;

import com.psddev.cms.db.RichTextElement;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("H6")
@RichTextElement.Tag(value = HeadingEnhancement6.TAG_NAME,
    menu = "Headings",
    block = true,
    position = -60.0)
public class HeadingEnhancement6 extends AbstractHeadingEnhancement {

    public static final String TAG_NAME = "h6";

    // -- Overrides -- //

    @Override
    protected String getTag() {
        return TAG_NAME;
    }
}
