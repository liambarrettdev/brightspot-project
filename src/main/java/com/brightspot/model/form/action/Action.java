package com.brightspot.model.form.action;

import com.brightspot.model.form.FormModule;
import com.psddev.dari.db.Recordable;

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
}
