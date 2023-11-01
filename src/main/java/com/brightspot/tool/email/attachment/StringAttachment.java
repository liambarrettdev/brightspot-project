package com.brightspot.tool.email.attachment;

import java.util.Optional;

public class StringAttachment extends Attachment {

    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public byte[] getBytes() {
        return Optional.ofNullable(getBody())
                .map(String::getBytes)
                .orElse(null);
    }
}
