package com.brightspot.model.rte;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.rte.RichTextModuleView;

public class RichTextModuleViewModel extends AbstractViewModel<RichTextModule> implements RichTextModuleView {

    @Override
    public Object getBody() {
        return buildRichTextView(model.getRichText());
    }
}
