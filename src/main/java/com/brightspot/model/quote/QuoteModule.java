package com.brightspot.model.quote;

import com.brightspot.model.image.Image;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Quote")
@ViewBinding(value = QuoteModuleViewModel.class, types = QuoteModule.VIEW_CLASS)
public class QuoteModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_CLASS = "quote-module";

    @Recordable.Indexed
    @Recordable.Required
    private String text;

    @Recordable.Indexed
    private String attribution;

    private Image image;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_CLASS;
    }

    @Override
    public String getLabel() {
        return getText();
    }
}
