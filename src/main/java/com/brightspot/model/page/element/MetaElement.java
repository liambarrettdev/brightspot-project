package com.brightspot.model.page.element;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.HtmlWriter;
import org.apache.commons.lang.StringUtils;

@ViewBinding(value = MetaElementViewModel.class, types = MetaElement.VIEW_TYPE)
public class MetaElement extends HeadElement {

    protected static final String VIEW_TYPE = "meta-element";

    @Recordable.Required
    private Type type;

    @DisplayName("Attribute: \"content\"")
    private String content;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return Optional.of(getType())
            .filter(NameType.class::isInstance)
            .map(Type::getValue)
            .orElse(null);
    }

    public String getHttpEquiv() {
        return Optional.of(getType())
            .filter(HttpEquivType.class::isInstance)
            .map(Type::getValue)
            .orElse(null);
    }

    public String getElementPreview() {
        StringWriter writer = new StringWriter();
        try {
            HtmlWriter htmlWriter = new HtmlWriter(writer);
            htmlWriter.writeStart("meta", "content", getContent());
            if (StringUtils.isNotBlank(getName())) {
                htmlWriter.writeStart("meta",
                    "name", getName(),
                    "content", getContent());
            }
            if (StringUtils.isNotBlank(getHttpEquiv())) {
                htmlWriter.writeStart("meta",
                    "http-equiv", getHttpEquiv(),
                    "content", getContent());
            }
            htmlWriter.writeEnd();
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

    @Recordable.Embedded
    public abstract static class Type extends Record {

        abstract String getValue();
    }

    @DisplayName("HTTP Equiv")
    public static class HttpEquivType extends Type {

        @Recordable.Required
        @DisplayName("Attribute: \"http-equiv\"")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @DisplayName("Name")
    public static class NameType extends Type {

        @Recordable.Required
        @DisplayName("Attribute: \"name\"")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
