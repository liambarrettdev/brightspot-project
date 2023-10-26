package com.brightspot.tool.email.attachment;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

public class UrlAttachment extends Attachment {

    private static final int CONNECT_TIMEOUT = 10 * 60 * 1000; // 10 mins

    @Override
    public byte[] getBytes() throws IOException {
        URLConnection connection = new URL(getUrl()).openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(CONNECT_TIMEOUT);

        byte[] bytes = IOUtils.toByteArray(connection.getInputStream());

        return Base64.getEncoder().encode(bytes);
    }
}
