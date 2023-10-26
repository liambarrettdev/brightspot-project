package com.brightspot.tool;

import java.io.IOException;

import com.psddev.cms.db.ElFunctionUtils;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.tool.ToolPageHead;

public class CustomToolPageHead implements ToolPageHead {

    public static final String CMS_CSS = "/_plugins/style/CMS.css";
    public static final String RTE_CSS = "/_plugins/style/RTE.css";

    // -- Overrides -- //

    @Override
    public void writeHtml(ToolPageContext page) throws IOException {
        page.writeTag("link",
            "rel", "stylesheet",
            "type", "text/css",
            "href", ElFunctionUtils.resource(CMS_CSS));

        page.writeTag("link",
            "rel", "stylesheet",
            "type", "text/css",
            "href", ElFunctionUtils.resource(RTE_CSS));
    }
}
