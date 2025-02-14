package com.brightspot.model.error.handler.redirect;

import java.io.IOException;
import java.io.Writer;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.model.error.handler.ErrorHandler;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.PageFilter;
import com.psddev.dari.util.HtmlWriter;
import com.psddev.dari.util.ObjectUtils;

public class RedirectErrorHandler extends ErrorHandler {

    private Content content;

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response, int statusCode) {
        if (!ObjectUtils.isBlank(content) && getStatusCodes().contains(statusCode)) {
            writeResponse(request, response);
            return true;
        }
        return false;
    }

    @Override
    public String getLabel() {
        StringBuilder builder = new StringBuilder();

        if (!getStatusCodes().isEmpty()) {
            builder.append(getStatusCodes().stream().map(Object::toString).collect(Collectors.joining(", "))).append(" | ");
        }

        if (!ObjectUtils.isBlank(content)) {
            builder.append(content.getLabel());
        }

        return builder.toString();
    }

    private void writeResponse(HttpServletRequest request, HttpServletResponse response) {
        try {
            Writer writer = new HtmlWriter(response.getWriter());
            PageFilter.renderObject(request, response, writer, content);
        } catch (IOException | ServletException e) {
            LOGGER.error("Could not write error response", e);
        }
    }
}
