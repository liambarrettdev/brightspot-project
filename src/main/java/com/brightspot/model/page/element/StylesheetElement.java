package com.brightspot.model.page.element;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@ViewBinding(value = StylesheetElementInlineViewModel.class, types = StylesheetElement.VIEW_TYPE_INLINE)
@ViewBinding(value = StylesheetElementExternalViewModel.class, types = StylesheetElement.VIEW_TYPE_EXTERNAL)
public class StylesheetElement extends HeadElement {

    protected static final String VIEW_TYPE_INLINE = "inline-stylesheet-element";
    protected static final String VIEW_TYPE_EXTERNAL = "external-stylesheet-element";

    @Recordable.Required
    private Stylesheet type = new InlineStylesheet();

    public Stylesheet getType() {
        return type;
    }

    public void setType(Stylesheet type) {
        this.type = type;
    }

    @Override
    public String getViewType() {
        return type instanceof InlineStylesheet ? VIEW_TYPE_INLINE : VIEW_TYPE_EXTERNAL;
    }

    @Recordable.Embedded
    public abstract static class Stylesheet extends Record {

    }

    @DisplayName("Inline")
    static class InlineStylesheet extends Stylesheet {

        @ToolUi.CodeType("text/javascript")
        private String body;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    @DisplayName("Link")
    static class ExternalStylesheet extends Stylesheet {

        private String link;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
