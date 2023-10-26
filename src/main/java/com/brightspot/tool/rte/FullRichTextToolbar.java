package com.brightspot.tool.rte;

import java.util.Arrays;
import java.util.List;

import com.psddev.cms.rte.RichTextToolbar;
import com.psddev.cms.rte.RichTextToolbarAction;
import com.psddev.cms.rte.RichTextToolbarItem;
import com.psddev.cms.rte.RichTextToolbarSeparator;
import com.psddev.cms.rte.RichTextToolbarStyle;

public class FullRichTextToolbar implements RichTextToolbar {

    // -- Overrides -- //

    @Override
    public List<RichTextToolbarItem> getItems() {
        return Arrays.asList(
            RichTextToolbarStyle.BOLD,
            RichTextToolbarStyle.ITALIC,
            RichTextToolbarStyle.UNDERLINE,
            RichTextToolbarStyle.STRIKETHROUGH,
            RichTextToolbarStyle.SUPERSCRIPT,
            RichTextToolbarStyle.SUBSCRIPT,
            RichTextToolbarStyle.LINK,
            RichTextToolbarStyle.HTML,
            RichTextToolbarAction.CLEAR,

            RichTextToolbarSeparator.BLOCK,

            RichTextToolbarStyle.UL,
            RichTextToolbarStyle.OL,

            RichTextToolbarSeparator.BLOCK,

            RichTextToolbarStyle.ALIGN_LEFT,
            RichTextToolbarStyle.ALIGN_CENTER,
            RichTextToolbarStyle.ALIGN_RIGHT,

            RichTextToolbarSeparator.BLOCK,

            RichTextToolbarAction.TABLE,
            RichTextToolbarItem.ELEMENTS,

            RichTextToolbarSeparator.BLOCK,

            RichTextToolbarAction.MODE,
            RichTextToolbarAction.FULLSCREEN
        );
    }
}
