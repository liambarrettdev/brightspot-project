package com.brightspot.model.error.handler;

import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Recordable.Embedded
public abstract class ErrorHandler extends Record {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    public abstract boolean handle(HttpServletRequest request, HttpServletResponse response, int statusCode);

    private Set<Integer> statusCodes;

    public Set<Integer> getStatusCodes() {
        if (statusCodes == null) {
            statusCodes = new HashSet<>();
        }
        return statusCodes;
    }

    public void setStatusCodes(Set<Integer> statusCodes) {
        this.statusCodes = statusCodes;
    }
}
