package com.brightspot.tool.email.attachment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class FileAttachment extends Attachment {

    @Override
    public byte[] getBytes() throws IOException {
        byte[] bytes = Files.readAllBytes(new File(getUrl()).toPath());

        return Base64.getEncoder().encode(bytes);
    }
}
