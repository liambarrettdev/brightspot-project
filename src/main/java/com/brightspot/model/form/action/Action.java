package com.brightspot.model.form.action;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.brightspot.model.form.FormModule;
import com.psddev.dari.db.Recordable;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Handles form submissions by triggering an action
 */
public interface Action extends Recordable {

    /**
     * Performs an action on form submission
     *
     * @param form the form that is being submitted
     * @return true if successful
     */
    boolean onSubmit(FormModule form);

    default String getRedirectTarget() {
        return null;
    }

    default List<NameValuePair> getFormParams(Map<String, String> submission) {
        return submission.entrySet().stream()
            .filter(Objects::nonNull)
            .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}
