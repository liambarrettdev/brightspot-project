package com.brightspot.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import com.amazonaws.util.CollectionUtils;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.gson.JsonElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

public final class HttpClientUtils {

    private HttpClientUtils() {
    }

    public static String getResponseContent(HttpResponse response) throws IOException {
        if (ObjectUtils.isBlank(response.getEntity())) {
            return "No Response Content";
        }

        return EntityUtils.toString(response.getEntity(), Charset.defaultCharset());
    }

    public static Integer getResponseStatus(HttpResponse response) {
        return Optional.ofNullable(response)
            .map(HttpResponse::getStatusLine)
            .map(StatusLine::getStatusCode)
            .orElse(null);
    }

    public static Boolean isSuccessfulResponse(Integer status) {
        // check if 2XX response
        if (ObjectUtils.isBlank(status)) {
            return false;
        }
        return (status / 100) == 2;
    }

    public static void setRequestEntity(
        HttpEntityEnclosingRequestBase request,
        JsonElement jsonContent,
        List<NameValuePair> urlParams) {
        if (!ObjectUtils.isBlank(jsonContent)) {
            request.setEntity(new StringEntity(jsonContent.toString(), ContentType.APPLICATION_JSON));
        }
        if (!CollectionUtils.isNullOrEmpty(urlParams)) {
            request.setEntity(new UrlEncodedFormEntity(urlParams, Charset.defaultCharset()));
        }
    }
}
