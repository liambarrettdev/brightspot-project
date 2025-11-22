package com.brightspot.model.faq;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("FAQ")
@ViewBinding(value = FaqModuleViewModel.class, types = FaqModule.VIEW_TYPE)
public class FaqModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_TYPE = "faq-module";

    @Recordable.Required
    private String title;

    private String description;

    @CollectionMinimum(1)
    private List<FaqSection> sections;

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

    public List<FaqSection> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    public void setSections(List<FaqSection> sections) {
        this.sections = sections;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }
}
