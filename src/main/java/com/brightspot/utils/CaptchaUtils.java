package com.brightspot.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.psddev.dari.util.CollectionUtils;
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

    private static final String SECRET_KEY = "secret";
    private static final String REMOTE_KEY = "remoteip";
    private static final String RESPONSE_KEY = "response";
    private static final String SUCCESS_KEY = "success";

    private CaptchaUtils() {
    }

    public static String sendVerifyRequest(String secret, String recaptcha, String remote)
        throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            if (StringUtils.isBlank(secret)) {
                return null;
            }

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(SECRET_KEY, secret));
            params.add(new BasicNameValuePair(RESPONSE_KEY, recaptcha));
            params.add(new BasicNameValuePair(REMOTE_KEY, remote));

            HttpPost httpPost = new HttpPost(CAPTCHA_VERIFY_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

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

    public static String getResponseScore(String response) {
        return Optional.ofNullable(ObjectUtils.fromJson(response))
            .map(data -> CollectionUtils.getByPath(data, SUCCESS_KEY))
            .map(Object::toString)
            .orElse(null);
    }

    public static Boolean verifySuccessResponse(String response) {
        if (StringUtils.isBlank(response)) {
            return null;
        }

        return Boolean.TRUE.equals(CollectionUtils.getByPath(ObjectUtils.fromJson(response), SUCCESS_KEY));
    }
}
