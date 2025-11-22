package com.brightspot.tool;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.tool.widget.AttachmentUrlWidget;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.Plugin;
import com.psddev.cms.tool.Tool;
import com.psddev.dari.db.Recordable;

@ToolUi.Hidden
@Recordable.DisplayName("Custom Plugins")
public class CustomTool extends Tool {

    @Override
    public List<Plugin> getPlugins() {
        List<Plugin> plugins = new ArrayList<>();

        plugins.add(new AttachmentUrlWidget());

        return plugins;
    }
}
