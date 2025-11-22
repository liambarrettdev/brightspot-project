package com.brightspot.model.page.element;

import com.brightspot.tool.field.annotation.Url;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@ViewBinding(value = ScriptElementInlineViewModel.class, types = ScriptElement.VIEW_TYPE_INLINE)
@ViewBinding(value = ScriptElementExternalViewModel.class, types = ScriptElement.VIEW_TYPE_EXTERNAL)
public class ScriptElement extends HeadElement {

    protected static final String VIEW_TYPE_INLINE = "inline-script-element";
    protected static final String VIEW_TYPE_EXTERNAL = "external-script-element";

    @Recordable.Required
    private Script type = new InlineScript();

    public Script getType() {
        return type;
    }

    public void setType(Script type) {
        this.type = type;
    }

    @Override
    public String getViewType() {
        return type instanceof InlineScript ? VIEW_TYPE_INLINE : VIEW_TYPE_EXTERNAL;
    }

    @Recordable.Embedded
    public abstract static class Script extends Record {

    }

    @DisplayName("Inline")
    static class InlineScript extends Script {

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
    static class ExternalScript extends Script {

        @Url
        private String src;

        private boolean async;

        private boolean defer;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public boolean isAsync() {
            return async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }

        public boolean isDefer() {
            return defer;
        }

        public void setDefer(boolean defer) {
            this.defer = defer;
        }
    }
}
