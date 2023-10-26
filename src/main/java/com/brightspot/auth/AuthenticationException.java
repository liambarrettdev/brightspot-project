package com.brightspot.auth;

import javax.servlet.ServletException;

public class AuthenticationException extends ServletException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
