package com.brightspot.integration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.brightspot.utils.HttpClientUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.TypeReference;
import com.psddev.dari.util.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericHttpClient.class);

    private static final TypeReference<Map<String, Object>> JSON_MAP_REF = new TypeReference<Map<String, Object>>() {

    };

    // -- Helper Methods -- //

    public Map<String, Object> parseJsonResponse(String response) throws JsonProcessingException {
        return StringUtils.isBlank(response) ? null : ObjectUtils.to(JSON_MAP_REF, response);
    }

    // -- Static Methods -- //

    public static String submitRequest(
        String url,
        Method method,
        JsonElement jsonContent,
        List<NameValuePair> urlParams,
        Map<String, String> headers) throws IOException, URISyntaxException {
        try (CloseableHttpClient client = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .build()) {
            HttpResponse response = client.execute(buildRequest(url, method, jsonContent, urlParams, headers));

            String responseContent = HttpClientUtils.getResponseContent(response);
            Integer responseStatus = HttpClientUtils.getResponseStatus(response);

            if (!HttpClientUtils.isSuccessfulResponse(responseStatus)) {
                LOGGER.error(
                    "Error calling the REST API\n\tEndpoint: {}\n\tResponse Code: {}\n\tResponse Content: {}",
                    url,
                    responseStatus,
                    responseContent);
                return null;
            }

            LOGGER.debug("Successfully executed REST API {} request: {}", method, url);

            return responseContent;
        }
    }

    public static HttpRequestBase buildRequest(
        String endpoint,
        Method method,
        JsonElement jsonContent,
        List<NameValuePair> urlParams,
        Map<String, String> headers) throws URISyntaxException {
        HttpRequestBase request;

        switch (method) {
            case DELETE:
                request = new HttpDelete(endpoint);
                break;
            case GET:
                URI uri = new URIBuilder(endpoint).addParameters(urlParams).build();
                request = new HttpGet(uri);
                break;
            case PATCH:
                request = new HttpPatch(endpoint);
                HttpClientUtils.setRequestEntity((HttpPatch) request, jsonContent, urlParams);
                break;
            case POST:
                request = new HttpPost(endpoint);
                HttpClientUtils.setRequestEntity((HttpPost) request, jsonContent, urlParams);
                break;
            case PUT:
                request = new HttpPut(endpoint);
                HttpClientUtils.setRequestEntity((HttpPut) request, jsonContent, urlParams);
                break;
            default:
                return null;
        }

        if (!ObjectUtils.isBlank(headers)) {
            headers.forEach(request::setHeader);
        }

        return request;
    }

    // -- Enums -- //

    public enum Method {
        DELETE,
        GET,
        PATCH,
        POST,
        PUT
    }
}
