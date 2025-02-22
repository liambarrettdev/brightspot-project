package com.brightspot.model.promo.list;

import com.brightspot.model.list.ListContent;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.brightspot.model.promo.list.type.CuratedListContent;
import com.brightspot.model.promo.list.type.DynamicListContent;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("List (Promos)")
@ViewBinding(value = ListModuleViewModel.class, types = ListModule.VIEW_CLASS)
public class ListModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_CLASS = "promo-list-module";

    private String title;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String description;

    @Types({ CuratedListContent.class, DynamicListContent.class })
    private ListContent content = new CuratedListContent();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ListContent getContent() {
        return content;
    }

    public void setContent(ListContent content) {
        this.content = content;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_CLASS;
    }

    @Override
    public String getLabel() {
        return getTitle();
    }
}
