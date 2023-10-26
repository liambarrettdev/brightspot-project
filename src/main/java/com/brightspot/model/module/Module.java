package com.brightspot.model.module;

import com.brightspot.model.rte.RichTextModule;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.ToolUi;

@ToolUi.Publishable(false)
public class Module extends Content {

    @Required
    private String name;

    @Required
    @Types(ShareableModule.class)
    private AbstractModule module = new RichTextModule();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AbstractModule getModule() {
        return module;
    }

    public void setModule(AbstractModule module) {
        this.module = module;
    }
}
