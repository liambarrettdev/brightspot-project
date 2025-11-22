package com.brightspot.model.faq;

import java.util.ArrayList;
import java.util.List;

import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
@Recordable.DisplayName("FAQ Section")
@ViewBinding(value = FaqSectionViewModel.class)
public class FaqSection extends Record {

    @Recordable.Required
    private String value;

    @CollectionMinimum(1)
    private List<FaqQuestion> questions;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<FaqQuestion> getQuestions() {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        return questions;
    }

    public void setQuestions(List<FaqQuestion> questions) {
        this.questions = questions;
    }

    // -- Overrides -- //

    @Override
    public String getLabel() {
        return getValue();
    }
}
