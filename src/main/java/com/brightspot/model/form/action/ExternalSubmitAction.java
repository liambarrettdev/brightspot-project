package com.brightspot.model.form.action;

import java.util.List;
import java.util.Map;

import com.brightspot.integration.GenericHttpClient;
import com.brightspot.model.form.FormModule;
import com.brightspot.tool.field.annotation.Url;
import com.google.common.base.Charsets;
import com.psddev.dari.db.Record;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.PageContextFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brightspot.integration.GenericHttpClient.Method.POST;

public class ExternalSubmitAction extends Record implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSubmitAction.class);

    @Url
    @Required
    private String endpoint;

    @Required
    private GenericHttpClient.Method method = POST;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public GenericHttpClient.Method getMethod() {
        return method;
    }

    public void setMethod(GenericHttpClient.Method method) {
        this.method = method;
    }

    // -- Overrides -- //

    @Override
    public boolean onSubmit(FormModule form) {
        Map<String, String> submission = form.getSubmission(PageContextFilter.Static.getRequestOrNull());
        List<NameValuePair> params = getFormParams(submission);
        if (ObjectUtils.isBlank(params)) {
            return true;
        }

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpRequestBase request = getHttpRequest(getEndpoint(), params);
            if (request == null) {
                return true;
            }

            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                LOGGER.error("External submission was unsuccessful: {}", EntityUtils.toString(response.getEntity()));
                return false;
            }
            LOGGER.info("Successfully executed request with response code {}", statusCode);
        } catch (Exception e) {
            LOGGER.error("Error executing external submission!", e);
            return false;
        }

        return true;
    }

    // -- Utility Methods -- //

    private HttpRequestBase getHttpRequest(String endpoint, List<NameValuePair> params) {
        if (StringUtils.isBlank(endpoint)) {
            return null;
        }

        HttpRequestBase request = null;
        switch (getMethod()) {
            case GET:
                request = new HttpGet(endpoint);
                break;
            case POST:
                request = new HttpPost(endpoint);
                ((HttpPost) request).setEntity(new UrlEncodedFormEntity(params, Charsets.UTF_8));
                break;
            default:
                break;
        }
        return request;
    }
}
