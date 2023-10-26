package com.brightspot.tool.email;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.tool.email.attachment.Attachment;
import com.psddev.dari.util.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMailMessage extends MailMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMailMessage.class);

    private List<Attachment> attachments;

    public CustomMailMessage() {
    }

    public CustomMailMessage(String to) {
        super(to);
    }

    public CustomMailMessage to(String to) {
        super.to(to);
        return this;
    }

    public CustomMailMessage from(String from) {
        super.from(from);
        return this;
    }

    public CustomMailMessage replyTo(String replyTo) {
        super.replyTo(replyTo);
        return this;
    }

    public CustomMailMessage subject(String subject) {
        super.subject(subject);
        return this;
    }

    public CustomMailMessage bodyPlain(String bodyPlain) {
        super.bodyPlain(bodyPlain);
        return this;
    }

    public CustomMailMessage bodyHtml(String bodyHtml) {
        super.bodyHtml(bodyHtml);
        return this;
    }

    public CustomMailMessage attachments(List<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public List<Attachment> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
