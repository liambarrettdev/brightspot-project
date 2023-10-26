package com.brightspot.model.rte.enhancement.navigation;

import java.io.IOException;

import com.brightspot.model.link.Link;
import com.brightspot.model.rte.enhancement.AbstractRichTextEnhancement;
import com.brightspot.tool.rte.RichTextProcessor;
import com.psddev.cms.db.RichTextElement;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.DisplayName(NavigationEnhancement.ELEMENT_NAME)
@RichTextElement.Tag(value = NavigationEnhancement.TAG_NAME,
    menu = AbstractRichTextEnhancement.MENU_GROUP,
    block = true,
    initialBody = NavigationEnhancement.ELEMENT_NAME,
    position = -50.0,
    preview = true,
    readOnly = true,
    root = true,
    keymaps = { "Ctrl-Shift-n" },
    tooltip = "Add Navigation"
)
@ToolUi.IconName(NavigationEnhancement.ICON_NAME)
@ViewBinding(value = NavigationEnhancementViewModel.class, types = RichTextProcessor.RICH_TEXT_ELEMENT_VIEW_TYPE)
public class NavigationEnhancement extends AbstractRichTextEnhancement {

    public static final String ELEMENT_NAME = "Navigation";
    public static final String TAG_NAME = "bsp.navigation";
    public static final String ICON_NAME = "switch_right";

    private String title;

    private Link previous;

    private Link next;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Link getPrevious() {
        return previous;
    }

    public void setPrevious(Link previous) {
        this.previous = previous;
    }

    public Link getNext() {
        return next;
    }

    public void setNext(Link next) {
        this.next = next;
    }

    // -- Overrides -- //

    @Override
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public void writePreviewHtml(ToolPageContext page) throws IOException {
        if (!ObjectUtils.isBlank(previous)) {
            page.writeStart("span", "style", "float: none; padding-left: 10px;");
            {
                page.writeStart("b");
                {
                    page.writeHtml("Previous: ");
                }
                page.writeEnd();
                page.writeHtml(previous.getText());

            }
            page.writeEnd();
        }

        if (!ObjectUtils.isBlank(next)) {
            page.writeStart("span", "style", "float: right; padding-right: 10px;");
            {
                page.writeStart("b");
                {
                    page.writeHtml("Next: ");
                }
                page.writeEnd();
                page.writeHtml(next.getText());
            }
            page.writeEnd();
        }
    }
}
