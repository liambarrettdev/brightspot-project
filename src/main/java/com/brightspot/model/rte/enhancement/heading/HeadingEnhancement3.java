package com.brightspot.model.rte.enhancement.heading;

import com.psddev.cms.db.RichTextElement;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("H3")
@RichTextElement.Tag(value = HeadingEnhancement3.TAG_NAME,
    menu = "Headings",
    block = true,
    position = -60.0)
public class HeadingEnhancement3 extends AbstractHeadingEnhancement {

    public static final String TAG_NAME = "h3";

    // -- Overrides -- //

    @Override
    protected String getTag() {
        return TAG_NAME;
    }
}
