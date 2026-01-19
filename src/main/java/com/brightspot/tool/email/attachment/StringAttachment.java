package com.brightspot.tool.email.attachment;

import java.nio.charset.StandardCharsets;
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
            .map(body -> body.getBytes(StandardCharsets.UTF_8))
            .orElse(null);
    }
}
