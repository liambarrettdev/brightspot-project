package com.brightspot.model.rte.enhancement.heading;

import com.psddev.cms.db.RichTextElement;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("H4")
@RichTextElement.Tag(value = HeadingEnhancement4.TAG_NAME,
    menu = "Headings",
    block = true,
    position = -60.0)
public class HeadingEnhancement4 extends AbstractHeadingEnhancement {

    public static final String TAG_NAME = "h4";

    // -- Overrides -- //

    @Override
    protected String getTag() {
        return TAG_NAME;
    }
}
