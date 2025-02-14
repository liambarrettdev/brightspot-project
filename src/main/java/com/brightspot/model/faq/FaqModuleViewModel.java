package com.brightspot.model.faq;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.faq.FaqModuleView;
import com.brightspot.view.model.faq.FaqSectionView;

public class FaqModuleViewModel extends AbstractViewModel<FaqModule> implements FaqModuleView {

    @Override
    public Object getTitle() {
        return model.getTitle();
    }

    @Override
    public Object getDescription() {
        return model.getDescription();
    }

    @Override
    public Collection<?> getSections() {
        return model.getSections().stream()
            .map(section -> createView(FaqSectionView.class, section))
            .collect(Collectors.toList());
    }
}
