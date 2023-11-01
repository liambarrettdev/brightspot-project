package com.brightspot.integration.google.captcha;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.brightspot.model.form.field.captcha.CaptchaProvider;
import com.brightspot.utils.CaptchaUtils;
import com.brightspot.utils.LocalizationUtils;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Record;
import org.apache.commons.lang.StringUtils;

@ViewBinding(value = GoogleCaptchaViewModel.class)
public class GoogleCaptchaProvider extends Record implements CaptchaProvider {

    @ToolUi.Note("")
    private String clientKey;

    @ToolUi.Secret
    @ToolUi.Note("")
    private String secretKey;

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    // --- Overrides --- //

    @Override
    public List<String> validate(HttpServletRequest request) {
        String remote = request.getRemoteAddr();
        String recaptchaText = request.getParameter(CaptchaUtils.RECAPTCHA_PARAM);

        try {
            if (StringUtils.isNotBlank(recaptchaText)) {
                String response = CaptchaUtils.sendVerifyRequest(getSecretKey(), recaptchaText, remote);
                if (Boolean.TRUE.equals(CaptchaUtils.verifySuccessResponse(response))) {
                    return null;
                }
            }
        } catch (IOException e) {
            return null;
        }

        return Collections.singletonList(LocalizationUtils.currentSiteText(this, "message.error.invalid"));
    }
}
