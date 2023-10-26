package com.brightspot.tool.email.attachment;

import java.io.IOException;
import javax.mail.internet.InternetHeaders;

public abstract class Attachment {

    public abstract byte[] getBytes() throws IOException;

    private String name;

    private String url;

    private String mimeType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    // -- Helper Methods -- //

    public InternetHeaders getHeaders() {
        InternetHeaders fileHeaders = new InternetHeaders();

        fileHeaders.setHeader("Content-Type", getMimeType() + "; name=\"" + getName() + "\"");
        fileHeaders.setHeader("Content-Transfer-Encoding", "base64");
        fileHeaders.setHeader("Content-Disposition", "attachment; filename=\"" + getName() + "\"");

        return fileHeaders;
    }
}
