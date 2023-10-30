package com.brightspot.model.html;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.brightspot.view.base.util.RawHtmlView;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Raw HTML")
@ViewBinding(value = HtmlModuleViewModel.class, types = HtmlModule.VIEW_CLASS)
public class HtmlModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_CLASS = "html-module";

    private String name;

    @ToolUi.CodeType("text/html")
    private String rawHtml;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRawHtml() {
        return rawHtml;
    }

    public void setRawHtml(String rawHtml) {
        this.rawHtml = rawHtml;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_CLASS;
    }

    // -- Statics -- //

    public static RawHtmlView of(String html) {
        return new RawHtmlView.Builder().html(html).build();
    }
}
