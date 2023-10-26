package com.brightspot.model.rte.enhancement.heading;

import com.brightspot.model.rte.enhancement.AbstractRichTextEnhancement;
import com.brightspot.tool.rte.RichTextProcessor;
import com.psddev.cms.db.RichTextElement;
import com.psddev.cms.view.ViewBinding;

@RichTextElement.Exclusive
@ViewBinding(value = HeadingEnhancementViewModel.class, types = RichTextProcessor.RICH_TEXT_ELEMENT_VIEW_TYPE)
public abstract class AbstractHeadingEnhancement extends AbstractRichTextEnhancement {

    private transient String text;

    protected abstract String getTag();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // -- Overrides -- //

    @Override
    protected String getElementName() {
        return null;
    }

    @Override
    public void fromBody(String body) {
        setText(body);
    }

    @Override
    public String toBody() {
        return getText();
    }
}
