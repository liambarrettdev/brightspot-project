package com.brightspot.model.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    public static final String CSRF_TOKEN_PARAM = "brightspot.form.csrf";

    protected static final String VIEW_TYPE = "form-module";

    private static final String DEFAULT_SUBMIT_BTN_LABEL = "Submit";

    private String name;

    @CollectionMinimum(1)
    private List<Field> fields;

    @Recordable.Embedded
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

    // -- Helper Methods -- //

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
        if (request == null) {
            return false;
        }

        // Validate form ID
        String formIdParam = request.getParameter(FORM_ID_PARAM);
        if (getId() == null || !getId().toString().equals(formIdParam)) {
            return false;
        }

        // Validate CSRF token
        String csrfToken = request.getParameter(CSRF_TOKEN_PARAM);
        HttpSession session = request.getSession(false);
        if (session == null || csrfToken == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(getCSRFSessionKey());
        return csrfToken.equals(sessionToken);
    }

    /**
     * Generates and stores a CSRF token in the session for this form.
     *
     * @param request the HTTP request containing the session
     * @return the generated CSRF token
     */
    public String generateCSRFToken(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        HttpSession session = request.getSession(true);
        String token = UUID.randomUUID().toString();
        session.setAttribute(getCSRFSessionKey(), token);
        return token;
    }

    /**
     * Gets the session key for storing this form's CSRF token.
     *
     * @return the session key
     */
    private String getCSRFSessionKey() {
        return CSRF_TOKEN_PARAM + "." + (getId() != null ? getId().toString() : "unknown");
    }
}
