package com.brightspot.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.gson.JsonElement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * Utility class for HTTP client operations.
 *
 * <p>This class provides helper methods for working with HTTP responses, including
 * extracting response content, checking status codes, and setting request entities.</p>
 */
public final class HttpClientUtils {

    private HttpClientUtils() {
    }

    /**
     * Extracts the response content from an HTTP response as a string.
     *
     * <p>Reads the response entity and converts it to a UTF-8 string. Returns
     * "No Response Content" if the response has no entity.</p>
     *
     * @param response the HTTP response
     * @return the response content as a UTF-8 string, or "No Response Content" if no entity
     * @throws IOException if an I/O error occurs while reading the response
     */
    public static String getResponseContent(HttpResponse response) throws IOException {
        if (ObjectUtils.isBlank(response.getEntity())) {
            return "No Response Content";
        }

        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }

    /**
     * Gets the HTTP status code from a response.
     *
     * <p>Extracts the status code from the response's status line. Returns
     * {@code null} if the response or status line is null.</p>
     *
     * @param response the HTTP response
     * @return the status code, or {@code null} if response is invalid
     */
    public static Integer getResponseStatus(HttpResponse response) {
        return Optional.ofNullable(response)
            .map(HttpResponse::getStatusLine)
            .map(StatusLine::getStatusCode)
            .orElse(null);
    }

    /**
     * Checks if an HTTP status code indicates a successful response.
     *
     * <p>Returns {@code true} if the status code is in the 2xx range (200-299),
     * {@code false} otherwise. Returns {@code false} if status is null or blank.</p>
     *
     * @param status the HTTP status code
     * @return {@code true} if status is 2xx, {@code false} otherwise
     */
    public static Boolean isSuccessfulResponse(Integer status) {
        // check if 2XX response
        if (ObjectUtils.isBlank(status)) {
            return false;
        }
        return (status / 100) == 2;
    }

    /**
     * Sets the request entity for an HTTP request.
     *
     * <p>Sets either JSON content or URL-encoded form parameters as the request entity.
     * If both are provided, URL-encoded form parameters take precedence (overwrites JSON).</p>
     *
     * <p>FIXME: The precedence logic (form params overwriting JSON) may be unexpected behavior - consider requiring only one type or making the behavior explicit</p>
     *
     * @param request the HTTP request to set the entity on
     * @param jsonContent the JSON content to send (optional)
     * @param urlParams the URL-encoded form parameters to send (optional, takes precedence over JSON)
     */
    public static void setRequestEntity(
        HttpEntityEnclosingRequestBase request,
        JsonElement jsonContent,
        List<NameValuePair> urlParams) {
        if (!ObjectUtils.isBlank(jsonContent)) {
            request.setEntity(new StringEntity(jsonContent.toString(), ContentType.APPLICATION_JSON));
        }
        if (CollectionUtils.isNotEmpty(urlParams)) {
            request.setEntity(new UrlEncodedFormEntity(urlParams, StandardCharsets.UTF_8));
        }
    }
}
