package com.brightspot.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CaptchaUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaUtils.class);

    public static final String CAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    public static final String RECAPTCHA_PARAM = "g-recaptcha-response";

    private CaptchaUtils() {
    }

    public static String sendVerifyRequest(String verifyUrl, String secret, String response, String remote)
        throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            if (StringUtils.isBlank(verifyUrl) || StringUtils.isBlank(secret)) {
                return null;
            }

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("secret", secret));
            params.add(new BasicNameValuePair("response", response));
            params.add(new BasicNameValuePair("remoteip", remote));

            HttpPost httpPost = new HttpPost(verifyUrl);
            httpPost.setEntity(new UrlEncodedFormEntity(params, Charset.defaultCharset()));

            LOGGER.debug("Sending request to [{}] = {}", verifyUrl, params);

            HttpResponse httpResponse = client.execute(httpPost);

            String responseContent = HttpClientUtils.getResponseContent(httpResponse);
            Integer responseStatus = HttpClientUtils.getResponseStatus(httpResponse);

            if (!HttpClientUtils.isSuccessfulResponse(responseStatus)) {
                LOGGER.error(
                    "Error verifying captcha\n Response Code: {}\n Response Content: {}",
                    responseStatus,
                    responseContent);
                return null;
            }

            return responseContent;
        }
    }

    public static String getResponseValue(String response, String field) {
        if (StringUtils.isBlank(response) || StringUtils.isBlank(field)) {
            return null;
        }

        Map<String, Object> responseMap = Utils.uncheckedCast(ObjectUtils.fromJson(response));
        if (responseMap.containsKey("success") && responseMap.containsKey(field)) {
            return responseMap.get(field).toString();
        }

        return null;
    }
}
