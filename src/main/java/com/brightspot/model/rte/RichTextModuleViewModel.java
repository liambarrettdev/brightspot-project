package com.brightspot.model.rte;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.rte.RichTextContentView;

public class RichTextModuleViewModel extends AbstractViewModel<RichTextModule> implements RichTextContentView {

    @Override
    public Object getBody() {
        return buildRichTextView(model.getRichText());
    }
}
