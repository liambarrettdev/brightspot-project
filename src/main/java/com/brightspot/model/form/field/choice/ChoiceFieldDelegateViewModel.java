package com.brightspot.model.form.field.choice;

import com.psddev.cms.view.DelegateView;
import com.psddev.cms.view.ViewModel;

public class ChoiceFieldDelegateViewModel extends ViewModel<ChoiceField> implements DelegateView {

    @Override
    public Object getDelegate() {
        if (model.getType() == null) {
            return null;
        }

        if (model.getType() instanceof ChoiceFieldType.Dropdown) {
            return createView(SelectInputViewModel.class, model);
        } else if (model.getType() instanceof ChoiceFieldType.Checkboxes || model.getType() instanceof ChoiceFieldType.RadioButtons) {
            return createView(ChoiceInputViewModel.class, model);
        }

        return null;
    }
}
