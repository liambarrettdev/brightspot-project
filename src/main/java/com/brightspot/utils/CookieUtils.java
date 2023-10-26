package com.brightspot.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.psddev.dari.util.JspUtils;
import org.apache.commons.lang3.StringUtils;

public final class CookieUtils {

    private CookieUtils() {
    }

    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        if (StringUtils.isBlank(cookieName)) {
            return null;
        }

        return Optional.ofNullable(request)
            .map(HttpServletRequest::getCookies)
            .flatMap(cookies -> Arrays.stream(cookies)
                .filter(Objects::nonNull)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst()
            )
            .orElse(null);
    }

    /**
     * Creates a new cookie and sets it in the response.
     *
     * @param response the response
     * @param cookieName the name of the cookie
     * @param cookieValue the value of the cookie
     * @param maxAge the max age of the cookie
     */
    public static void setCookie(
        HttpServletRequest request,
        HttpServletResponse response,
        String cookieName,
        String cookieValue,
        int maxAge) {
        if (StringUtils.isBlank(cookieName)) {
            return;
        }

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(JspUtils.isSecure(request));
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    /**
     * Creates a new signed cookie and sets it in the response.
     *
     * @param response the response
     * @param cookieName the name of the cookie
     * @param cookieValue the value of the cookie
     * @param maxAge the max age of the cookie
     */
    public static void setSignedCookie(
        HttpServletRequest request,
        HttpServletResponse response,
        String cookieName,
        String cookieValue,
        int maxAge) {
        if (StringUtils.isBlank(cookieName)) {
            return;
        }

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(JspUtils.isSecure(request));
        cookie.setPath("/");

        JspUtils.setSignedCookie(response, cookie);
    }
}
