package com.brightspot.integration.stripe;

import com.brightspot.tool.field.processor.EncryptedFieldProcessor;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
public class StripeSettings extends Record {

    @ToolUi.Secret
    @ToolUi.DisplayName("API Key")
    private String apiKey;

    @ToolUi.Secret
    @ToolUi.DisplayName("API Secret")
    @ToolUi.InputProcessorPath(EncryptedFieldProcessor.PATH)
    private String apiSecret;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
}
