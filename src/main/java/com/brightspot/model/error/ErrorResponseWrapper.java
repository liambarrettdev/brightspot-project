package com.brightspot.model.error;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.brightspot.model.error.handler.ErrorHandler;

public class ErrorResponseWrapper extends HttpServletResponseWrapper {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Set<ErrorHandler> handlers;

    public ErrorResponseWrapper(HttpServletRequest request, HttpServletResponse response, Set<ErrorHandler> handlers) {
        super(response);

        this.request = request;
        this.response = response;
        this.handlers = handlers;
    }

    @Override
    public void sendError(int statusCode, String msg) {
        sendError(statusCode);
    }

    @Override
    public void sendError(int statusCode) {
        response.setStatus(statusCode);

        for (ErrorHandler handler : handlers) {
            if (handler.handle(request, response, statusCode)) {
                return;
            }
        }
    }
}
