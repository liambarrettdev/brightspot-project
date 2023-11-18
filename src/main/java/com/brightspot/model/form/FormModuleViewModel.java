package com.brightspot.model.form;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.form.action.Action;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.view.model.form.FormModuleView;
import com.brightspot.view.model.form.input.HiddenInputView;
import com.psddev.cms.view.ViewResponse;
import com.psddev.cms.view.servlet.HttpMethod;
import com.psddev.dari.db.State;
import org.apache.commons.lang3.StringUtils;

import static com.brightspot.integration.GenericHttpClient.Method.POST;

public class FormModuleViewModel extends AbstractViewModel<FormModule> implements FormModuleView {

    @HttpMethod
    private String method;

    private String successMessage;

    private String errorMessage;

    private String action;

    @Override
    protected void onCreate(ViewResponse response) {
        super.onCreate(response);

        if (POST.name().equalsIgnoreCase(method)) {
            processFormSubmission();
        }

        action = model.getActions().stream()
            .map(Action::getRedirectTarget)
            .filter(StringUtils::isNotBlank)
            .findAny()
            .orElse(null);
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

    @Override
    public Object getAction() {
        return action;
    }

    private void processFormSubmission() {
        boolean succeeded = true;

        for (Action action : model.getActions()) {
            if (!action.onSubmit(model)) {
                succeeded = false;
                break;
            }
        }

        if (succeeded) {
            successMessage = LocalizationUtils.currentSiteText(model, "successMessage");
        } else {
            errorMessage = LocalizationUtils.currentSiteText(model, "errorMessage");
        }
    }
}
