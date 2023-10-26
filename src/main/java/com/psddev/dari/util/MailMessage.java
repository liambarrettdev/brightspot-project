package com.psddev.dari.util;

import com.brightspot.tool.CustomGlobalSettings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple email message class with builder pattern support
 **/
public class MailMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailMessage.class);

    private String to;
    private String from;
    private String replyTo;
    private String subject;
    private String bodyPlain;
    private String bodyHtml;

    public MailMessage() {
    }

    public MailMessage(String to) {
        this.to = to;
    }

    public MailMessage to(String to) {
        this.to = to;
        return this;
    }

    public MailMessage from(String from) {
        this.from = from;
        return this;
    }

    public MailMessage replyTo(String replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    public MailMessage subject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailMessage bodyPlain(String bodyPlain) {
        this.bodyPlain = bodyPlain;
        return this;
    }

    public MailMessage bodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
        return this;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyPlain() {
        return bodyPlain;
    }

    public void setBodyPlain(String bodyPlain) {
        this.bodyPlain = bodyPlain;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    /**
     * Sends mail via MailProvider default, from settings.
     */
    public void send() {
        // if email delivery is disabled, forward all emails to configured fallback addresses
        if (CustomGlobalSettings.get(CustomGlobalSettings::isDisableEmailDelivery)) {
            String fallbackEmail = CustomGlobalSettings.get(CustomGlobalSettings::getFallbackEmail);
            if (StringUtils.isBlank(fallbackEmail)) {
                LOGGER.warn("Email delivery is disabled");
                return;
            }
            this.to(fallbackEmail);
        }
        MailProvider.Static.getDefault().send(this);
    }
}
