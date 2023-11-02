package com.brightspot.model.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import com.brightspot.model.form.action.Action;
import com.brightspot.model.form.field.Field;
import com.brightspot.model.form.field.SubmittableField;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.DisplayName("Form")
@ViewBinding(value = FormModuleViewModel.class, types = FormModule.VIEW_TYPE)
public class FormModule extends AbstractModule implements ShareableModule {

    public static final String TAB_OVERRIDES = "Overrides";
    public static final String FORM_ID_PARAM = "brightspot.form.id";

    protected static final String VIEW_TYPE = "form-module";

    private static final String DEFAULT_SUBMIT_BTN_LABEL = "Submit";

    private String name;

    @CollectionMinimum(1)
    private List<Field> fields;

    @Embedded
    @CollectionMinimum(1)
    private List<Action> actions;

    @ToolUi.Tab(TAB_OVERRIDES)
    @ToolUi.Placeholder(DEFAULT_SUBMIT_BTN_LABEL)
    private String submitButtonLabel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Action> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getSubmitButtonLabel() {
        return ObjectUtils.firstNonBlank(submitButtonLabel, DEFAULT_SUBMIT_BTN_LABEL);
    }

    public void setSubmitButtonLabel(String submitButtonLabel) {
        this.submitButtonLabel = submitButtonLabel;
    }
    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public String getLabel() {
        return getName();
    }

    // --- Helper Methods --- //

    public Map<String, String> getSubmission(HttpServletRequest request) {
        if (!validateRequest(request)) {
            return null;
        }

        Map<String, String> submission = new HashMap<>();
        for (Field field : getFields()) {
            Optional.ofNullable(field)
                .filter(SubmittableField.class::isInstance)
                .map(SubmittableField.class::cast)
                .map(f -> f.getSubmittedValue(request))
                .ifPresent(submission::putAll);
        }
        return submission;
    }

    private boolean validateRequest(HttpServletRequest request) {
        return Optional.ofNullable(request)
            .map(r -> r.getParameter(FORM_ID_PARAM))
            .map(id -> getId().toString().equals(id))
            .orElse(false);
    }
}
