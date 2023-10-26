package com.brightspot.model.form.field.hidden;

import com.brightspot.project.view.model.form.input.HiddenInputView;
import com.psddev.cms.view.ViewModel;

public class HiddenInputViewModel extends ViewModel<HiddenField> implements HiddenInputView {

    @Override
    public Object getName() {
        return model.getParameterName();
    }

    @Override
    public Object getValue() {
        return model.getValue();
    }
}
