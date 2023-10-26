package com.brightspot.model.rte.enhancement.quote;

import java.io.IOException;

import com.brightspot.model.quote.QuoteModule;
import com.brightspot.model.rte.enhancement.AbstractRichTextEnhancement;
import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.tool.rte.RichTextProcessor;
import com.psddev.cms.db.RichTextElement;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName(QuoteEnhancement.ELEMENT_NAME)
@RichTextElement.Tag(value = QuoteEnhancement.TAG_NAME,
    menu = AbstractRichTextEnhancement.MENU_GROUP,
    block = true,
    initialBody = QuoteEnhancement.ELEMENT_NAME,
    position = -50.0,
    preview = true,
    readOnly = true,
    root = true,
    keymaps = { "Ctrl-Shift-q" },
    tooltip = "Add Quote"
)
@ToolUi.IconName(QuoteEnhancement.ICON_NAME)
@ViewBinding(value = QuoteEnhancementViewModel.class, types = RichTextProcessor.RICH_TEXT_ELEMENT_VIEW_TYPE)
public class QuoteEnhancement extends AbstractRichTextEnhancement implements Alignable {

    public static final String ELEMENT_NAME = "Quote";
    public static final String TAG_NAME = "bsp.blockquote";
    public static final String ICON_NAME = "format_quote";

    private QuoteModule quote;

    public QuoteModule getQuote() {
        return quote;
    }

    public void setQuote(QuoteModule quote) {
        this.quote = quote;
    }

    // -- Overrides -- //

    @Override
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public void writePreviewHtml(ToolPageContext page) throws IOException {
        if (getQuote() != null) {
            String text = getQuote().getText();
            if (text != null) {
                page.writeStart("i");
                page.write(text);
                page.writeEnd();
            }
        }
    }
}
