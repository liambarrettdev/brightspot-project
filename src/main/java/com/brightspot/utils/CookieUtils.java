package com.brightspot.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.psddev.dari.util.JspUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for HTTP cookie operations.
 *
 * <p>This class provides methods to get and set HTTP cookies in servlet requests and
 * responses. It handles both regular cookies and signed cookies for security.</p>
 *
 * <p>All cookies set by this class are configured with:</p>
 * <ul>
     *   <li>HttpOnly flag enabled (prevents XSS attacks)</li>
     *   <li>Secure flag set based on request security (HTTPS only in production)</li>
     *   <li>Path set to "/" (available site-wide)</li>
     * </ul>
 */
public final class CookieUtils {

    private CookieUtils() {
    }

    /**
     * Retrieves a cookie from the HTTP request by name.
     *
     * <p>Returns the first cookie matching the given name, or {@code null} if no matching
     * cookie is found or if the cookie name is blank.</p>
     *
     * @param request the HTTP servlet request
     * @param cookieName the name of the cookie to retrieve
     * @return the Cookie object, or {@code null} if not found or name is blank
     */
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
     * <p>The cookie is configured with HttpOnly enabled, Secure flag based on request
     * security, path set to "/", and the specified max age. If the cookie name is blank,
     * the method returns without setting a cookie.</p>
     *
     * @param request the HTTP servlet request (used to determine if connection is secure)
     * @param response the HTTP servlet response to add the cookie to
     * @param cookieName the name of the cookie (must not be blank)
     * @param cookieValue the value of the cookie
     * @param maxAge the max age of the cookie in seconds (0 to delete cookie)
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
     * <p>The cookie is signed using {@link com.psddev.dari.util.JspUtils#setSignedCookie}
     * to prevent tampering. The cookie is configured with HttpOnly enabled, Secure flag
     * based on request security, path set to "/", and the specified max age. If the
     * cookie name is blank, the method returns without setting a cookie.</p>
     *
     * <p>Signed cookies are more secure than regular cookies as they include a signature
     * that can be verified to ensure the cookie value has not been modified.</p>
     *
     * @param request the HTTP servlet request (used to determine if connection is secure)
     * @param response the HTTP servlet response to add the cookie to
     * @param cookieName the name of the cookie (must not be blank)
     * @param cookieValue the value of the cookie (will be signed)
     * @param maxAge the max age of the cookie in seconds (0 to delete cookie)
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
