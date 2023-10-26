package com.brightspot.model.gallery;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Gallery")
@ViewBinding(value = GalleryModuleViewModel.class, types = GalleryModule.VIEW_CLASS)
public class GalleryModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_CLASS = "gallery-module";

    private String name;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String description;

    private List<Slide> slides;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Slide> getSlides() {
        if (slides == null) {
            slides = new ArrayList<>();
        }
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_CLASS;
    }

    @Override
    public String getLabel() {
        return getName();
    }
}
