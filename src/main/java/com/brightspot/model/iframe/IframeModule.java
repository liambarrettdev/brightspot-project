package com.brightspot.model.iframe;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.brightspot.tool.field.annotation.Url;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.DisplayName("Iframe")
@ViewBinding(value = IframeModuleViewModel.class, types = IframeModule.VIEW_CLASS)
public class IframeModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_CLASS = "iframe-module";

    private static final String DEFAULT_HEIGHT = "600";
    private static final String TAB_OVERRIDES = "Overrides";

    @ToolUi.CssClass("is-half")
    @ToolUi.Placeholder(DEFAULT_HEIGHT)
    @Minimum(1)
    private Integer height;

    @ToolUi.CssClass("is-half")
    private Integer width;

    @Url
    private String url;

    @ToolUi.Tab(TAB_OVERRIDES)
    private String name;

    @ToolUi.Tab(TAB_OVERRIDES)
    @Values({
        "allow-forms",
        "allow-pointer-lock",
        "allow-popups",
        "allow-same-origin",
        "allow-scripts ",
        "allow-top-navigation" })
    private String sandbox;

    public Integer getHeight() {
        return ObjectUtils.firstNonBlank(height, Integer.parseInt(DEFAULT_HEIGHT));
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return ObjectUtils.firstNonBlank(width, 0);
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSandbox() {
        return sandbox;
    }

    public void setSandbox(String sandbox) {
        this.sandbox = sandbox;
    }
    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_CLASS;
    }
}
