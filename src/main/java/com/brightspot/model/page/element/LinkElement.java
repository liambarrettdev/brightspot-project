package com.brightspot.model.page.element;

import java.io.IOException;
import java.io.StringWriter;

import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.HtmlWriter;

@ViewBinding(value = LinkElementViewModel.class, types = LinkElement.VIEW_TYPE)
public class LinkElement extends HeadElement {

    protected static final String VIEW_TYPE = "link-element";

    @DisplayName("Attribute: \"rel\"")
    private String rel;

    @DisplayName("Attribute: \"href\"")
    private String href;

    @DisplayName("Attribute: \"as\"")
    private String asAttribute;

    @DisplayName("Cross-Origin?")
    private Boolean crossOrigin;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getAsAttribute() {
        return asAttribute;
    }

    public void setAsAttribute(String asAttribute) {
        this.asAttribute = asAttribute;
    }

    public Boolean isCrossOrigin() {
        return Boolean.TRUE.equals(crossOrigin);
    }

    public void setCrossOrigin(Boolean crossOrigin) {
        this.crossOrigin = crossOrigin;
    }

    public String getElementPreview() {
        StringWriter writer = new StringWriter();

        try {
            new HtmlWriter(writer).writeTag("link",
                "rel", getRel(),
                "href", getHref(),
                "as", getAsAttribute());
        } catch (IOException e) {
            return super.getLabel();
        }

        return writer.toString();
    }

    // --- Overrides --- //

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public String getLabel() {
        return getElementPreview();
    }
}
