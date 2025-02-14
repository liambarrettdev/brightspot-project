package com.brightspot.model.faq;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.faq.FaqQuestionView;
import com.brightspot.view.model.faq.FaqSectionView;

public class FaqSectionViewModel extends AbstractViewModel<FaqSection> implements FaqSectionView {

    @Override
    public Object getHeading() {
        return model.getValue();
    }

    @Override
    public Collection<?> getQuestions() {
        return model.getQuestions().stream()
            .map(question -> createView(FaqQuestionView.class, question))
            .collect(Collectors.toList());
    }
}
