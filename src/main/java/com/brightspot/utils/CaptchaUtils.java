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

/**
 * Utility class for Google reCAPTCHA verification.
 *
 * <p>This class provides methods to verify reCAPTCHA responses by sending verification
 * requests to Google's reCAPTCHA API and parsing the response to determine if the
 * verification was successful.</p>
 *
 * <p>All methods handle network errors gracefully and return {@code null} on failure.</p>
 */
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

    /**
     * Sends a verification request to Google's reCAPTCHA API.
     *
     * <p>This method sends a POST request to Google's reCAPTCHA verification endpoint
     * with the secret key, reCAPTCHA response token, and remote IP address. Returns
     * the JSON response from Google, or {@code null} if the secret is blank or if
     * the HTTP request fails.</p>
     *
     * <p>TODO: Consider adding retry logic for transient network failures</p>
     * <p>FIXME: Error handling could be improved - currently returns null on any error, making it difficult to distinguish between different failure types</p>
     *
     * @param secret the reCAPTCHA secret key
     * @param recaptcha the reCAPTCHA response token from the user
     * @param remote the remote IP address of the user
     * @return the JSON response string from Google, or {@code null} if verification fails
     * @throws IOException if an I/O error occurs during the HTTP request
     */
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

    /**
     * Extracts the success score from a reCAPTCHA verification response.
     *
     * <p>Parses the JSON response and extracts the "success" field value as a string.
     * Returns {@code null} if the response is invalid or cannot be parsed.</p>
     *
     * <p>TODO: Consider returning a Boolean instead of String for better type safety</p>
     *
     * @param response the JSON response string from Google's reCAPTCHA API
     * @return the success value as a string, or {@code null} if response is invalid
     */
    public static String getResponseScore(String response) {
        return Optional.ofNullable(ObjectUtils.fromJson(response))
            .map(data -> CollectionUtils.getByPath(data, SUCCESS_KEY))
            .map(Object::toString)
            .orElse(null);
    }

    /**
     * Verifies if a reCAPTCHA response indicates successful verification.
     *
     * <p>Parses the JSON response and checks if the "success" field is {@code true}.
     * Returns {@code null} if the response is blank or cannot be parsed.</p>
     *
     * @param response the JSON response string from Google's reCAPTCHA API
     * @return {@code true} if verification was successful, {@code false} if unsuccessful,
     *         or {@code null} if response is blank or invalid
     */
    public static Boolean verifySuccessResponse(String response) {
        if (StringUtils.isBlank(response)) {
            return null;
        }

        return Boolean.TRUE.equals(CollectionUtils.getByPath(ObjectUtils.fromJson(response), SUCCESS_KEY));
    }
}
