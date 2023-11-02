package com.brightspot.model.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.view.model.form.FormView;
import com.brightspot.view.model.form.input.HiddenInputView;
import com.psddev.cms.view.ViewResponse;
import com.psddev.cms.view.servlet.HttpMethod;
import com.psddev.dari.db.State;
import org.apache.commons.collections4.CollectionUtils;

import static com.brightspot.integration.GenericHttpClient.Method.*;

public class FormModuleViewModel extends AbstractViewModel<FormModule> implements FormView {

    @HttpMethod
    private String method;

    private String successMessage;

    private String errorMessage;

    @Override
    protected void onCreate(ViewResponse response) {
        super.onCreate(response);

        if (POST.name().equalsIgnoreCase(method)) {
            processFormSubmission();
        }
    }

    @Override
    public Object getTitle() {
        return model.getName();
    }

    @Override
    public Collection<?> getInputs() {
        List<Object> items = model.getFields().stream()
            .map(input -> createView(input.getViewClass(), input))
            .collect(Collectors.toList());

        items.add(new HiddenInputView.Builder()
            .name(FormModule.FORM_ID_PARAM)
            .value(State.getInstance(model).getId().toString())
            .build());

        return items;
    }

    @Override
    public Object getSubmitLabel() {
        return model.getSubmitButtonLabel();
    }

    @Override
    public Object getSuccessMessage() {
        return successMessage;
    }

    @Override
    public Object getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Object getMethod() {
        return POST.name();
    }

    private void processFormSubmission() {
        List<String> errors = new ArrayList<>();

        model.getActions().forEach(action -> CollectionUtils.addIgnoreNull(errors, action.onSubmit(model)));

        if (!errors.isEmpty()) {
            errorMessage = String.join("\\n", errors);
        } else {
            successMessage = LocalizationUtils.currentSiteText(model, "successMessage");
        }
    }
}
