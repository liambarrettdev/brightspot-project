package com.brightspot.model.form.action;

import java.util.Map;

import com.brightspot.model.form.FormModule;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.MailMessage;
import com.psddev.dari.util.PageContextFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailAction extends Record implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailAction.class);

    @Recordable.Required
    private String fromEmail;

    @Recordable.Required
    @ToolUi.Note("Specify the recipient address for the email. To use a parameter, use ${parameterName}.")
    private String toEmail;

    @Recordable.Required
    @ToolUi.Note("Specify the subject for the email. To use a parameter, use ${parameterName}.")
    private String subject;

    @Recordable.Required
    @ToolUi.Note("Send an email with the values from the form. To use a parameter, use ${parameterName}.")
    private String body;

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // -- Overrides -- //

    @Override
    public boolean onSubmit(FormModule form) {
        try {
            String to = getToEmail();
            String subject = getSubject();
            String body = getBody();

            Map<String, String> submission = form.getSubmission(PageContextFilter.Static.getRequestOrNull());
            for (Map.Entry<String, String> entry : submission.entrySet()) {
                String key = "${" + entry.getKey() + "}";
                String value = sanitizeValue(entry.getValue());

                if (StringUtils.isNotBlank(to)) {
                    to = to.replace(key, value);
                }
                if (StringUtils.isNotBlank(subject)) {
                    subject = subject.replace(key, value);
                }
                if (StringUtils.isNotBlank(body)) {
                    body = body.replace(key, value);
                }
            }

            new MailMessage()
                .from(getFromEmail())
                .to(to)
                .subject(subject)
                .bodyPlain(body)
                .send();
        } catch (RuntimeException e) {
            LOGGER.error("Cannot send mail message", e);
            return false;
        }

        return true;
    }

    // -- Helper Methods -- //

    /**
     * Sanitizes user input to prevent email header injection attacks
     * by removing carriage return and newline characters.
     *
     * @param value the user input value to sanitize
     * @return the sanitized value, or empty string if input is null
     */
    private String sanitizeValue(String value) {
        if (value == null) {
            return "";
        }
        // Remove \r, \n, and any Unicode line separators to prevent header injection
        return value.replaceAll("[\\r\\n\\u0085\\u2028\\u2029]", "");
    }
}
