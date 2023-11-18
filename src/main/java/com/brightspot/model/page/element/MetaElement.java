package com.brightspot.model.page.element;

import java.io.IOException;
import java.io.StringWriter;

import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.HtmlWriter;

@ViewBinding(value = MetaElementViewModel.class, types = MetaElement.VIEW_TYPE)
public class MetaElement extends HeadElement {

    protected static final String VIEW_TYPE = "meta-element";

    @Required
    @DisplayName("Attribute: \"name\"")
    private String name;

    @DisplayName("Attribute: \"content\"")
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getElementPreview() {
        StringWriter writer = new StringWriter();

        try {
            new HtmlWriter(writer).writeTag("meta",
                "name", getName(),
                "content", getContent());
        } catch (IOException e) {
            return super.getLabel();
        }

        return writer.toString();
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public String getLabel() {
        return getElementPreview();
    }
}
