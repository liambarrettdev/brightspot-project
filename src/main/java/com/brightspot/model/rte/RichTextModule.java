package com.brightspot.model.rte;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.brightspot.tool.rte.FullRichTextToolbar;
import com.brightspot.utils.RichTextUtils;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.crosslinker.db.Crosslinkable;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Rich Text")
@Crosslinkable.SimulationName("Default")
@ViewBinding(value = RichTextModuleViewModel.class, types = RichTextModule.VIEW_TYPE)
public class RichTextModule extends AbstractModule implements Crosslinkable, ShareableModule {

    protected static final String VIEW_TYPE = "rte-module";

    @Crosslinkable.Crosslinked
    @ToolUi.RichText(toolbar = FullRichTextToolbar.class, inline = false)
    private String richText;

    public String getRichText() {
        return richText;
    }

    public void setRichText(String richText) {
        this.richText = richText;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public String getLabel() {
        return RichTextUtils.getFirstBodyParagraph(getRichText());
    }
}
