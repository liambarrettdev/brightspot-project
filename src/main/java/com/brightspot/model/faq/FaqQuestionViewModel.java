package com.brightspot.model.faq;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.faq.FaqQuestionView;

public class FaqQuestionViewModel extends AbstractViewModel<FaqQuestion> implements FaqQuestionView {

    @Override
    public Object getQuestion() {
        return model.getQuestion();
    }

    @Override
    public Object getAnswer() {
        return buildRichTextView(model.getAnswer());
    }
}
