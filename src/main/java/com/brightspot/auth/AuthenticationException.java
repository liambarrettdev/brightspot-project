package com.brightspot.auth;

import javax.servlet.ServletException;

public class AuthenticationException extends ServletException {

    public AuthenticationException(String message) {
        super(message);
    }
}
