package com.brightspot.model.faq;

import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
@Recordable.DisplayName("FAQ Question")
@ViewBinding(value = FaqQuestionViewModel.class)
public class FaqQuestion extends Record {

    @Required
    private String question;

    @Required
    @ToolUi.RichText(inline = false, toolbar = BasicRichTextToolbar.class)
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    // --- Overrides --- //

    @Override
    public String getLabel() {
        return getQuestion();
    }
}
