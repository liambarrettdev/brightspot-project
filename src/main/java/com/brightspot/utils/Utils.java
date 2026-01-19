package com.brightspot.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;

import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUser;
import com.psddev.cms.tool.AuthenticationFilter;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.PageContextFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * General utility class providing common helper methods.
 *
 * <p>This class provides a variety of utility methods for string manipulation,
 * URL encoding/decoding, type conversion, and common operations used throughout
 * the application.</p>
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Normalizes a string for use in URLs, filenames, etc.
     *
     * <p>Delegates to {@link com.psddev.dari.util.StringUtils#toNormalized(CharSequence)}
     * which typically converts to lowercase, removes special characters, and replaces
     * spaces with hyphens.</p>
     *
     * @param string the string to normalize
     * @return the normalized string
     */
    public static String toNormalized(CharSequence string) {
        return com.psddev.dari.util.StringUtils.toNormalized(string);
    }

    /**
     * Gets the current site from the current tool user.
     *
     * <p>Retrieves the current tool user and returns their current site, or
     * {@code null} if no user is logged in.</p>
     *
     * @return the current site, or {@code null} if no user is logged in
     */
    public static Site getCurrentSite() {
        ToolUser user = getCurrentToolUser();
        return user != null ? user.getCurrentSite() : null;
    }

    /**
     * Gets the current tool user from the page context.
     *
     * <p>Retrieves the HTTP request from the page context and extracts the
     * authenticated user. Returns {@code null} if no request is available
     * or no user is authenticated.</p>
     *
     * @return the current tool user, or {@code null} if not available
     */
    public static ToolUser getCurrentToolUser() {
        return getCurrentToolUser(PageContextFilter.Static.getRequestOrNull());
    }

    /**
     * Gets the current tool user from an HTTP request.
     *
     * <p>Uses the AuthenticationFilter to extract the authenticated user from
     * the request. Returns {@code null} if the request is null or no user is
     * authenticated.</p>
     *
     * @param request the HTTP servlet request
     * @return the current tool user, or {@code null} if not available
     */
    public static ToolUser getCurrentToolUser(HttpServletRequest request) {
        return request != null ? AuthenticationFilter.Static.getUser(request) : null;
    }

    /**
     * Extracts a numeric value from a string.
     *
     * <p>Removes all non-numeric characters (except decimal points) and converts
     * the result to the requested numeric type. Returns {@code null} if the input
     * is blank.</p>
     *
     * <p>TODO: Consider using a more robust number parsing library that handles locale-specific decimal separators</p>
     *
     * @param <T> the numeric type
     * @param input the string to extract the number from
     * @param clazz the target numeric class
     * @return the numeric value, or {@code null} if input is blank
     */
    public static <T extends Number> T numericValue(String input, Class<T> clazz) {
        if (StringUtils.isBlank(input)) {
            return null;
        }

        Number value = NumberUtils.createNumber(input.replaceAll("[^0-9.]", ""));
        return ObjectUtils.to(clazz, value);
    }

    /**
     * Finds a map entry and converts it to the requested type.
     *
     * <p>Looks up the key in the map, converts the value to the requested type,
     * and returns it. Returns the fallback value if the map is null, the key is
     * not found, or the value cannot be converted.</p>
     *
     * @param <T> the target type
     * @param key the map key to look up
     * @param content the map to search in
     * @param clazz the target class to convert to
     * @param fallback the fallback value if not found or conversion fails
     * @return the converted value, or the fallback if not found
     */
    public static <T> T findMapEntry(String key, Map<String, Object> content, Class<T> clazz, T fallback) {
        return Optional.ofNullable(content)
            .map(m -> m.get(key))
            .map(e -> ObjectUtils.to(clazz, e))
            .orElse(fallback);
    }

    /**
     * Creates a predicate that filters duplicate elements based on a key extractor.
     *
     * <p>Returns a predicate that can be used with Stream.filter() to remove duplicates
     * based on a key. Uses a ConcurrentHashMap to track seen keys for thread-safety.</p>
     *
     * <p>TODO: Consider adding a size limit to prevent memory issues with very large streams</p>
     *
     * @param <T> the element type
     * @param keyExtractor the function to extract the key from each element
     * @return a predicate that returns true for the first occurrence of each key
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Converts seconds to a human-readable time string.
     *
     * <p>Formats the time as "MM mins SS secs" where MM is minutes and SS is seconds.
     * The format is fixed-width with zero-padding.</p>
     *
     * <p>TODO: Consider supporting hours and days for longer durations</p>
     *
     * @param seconds the number of seconds
     * @return a formatted time string (e.g., "05 mins 30 secs")
     */
    public static String convertTime(long seconds) {
        return String.format("%02d mins %02d secs", seconds / 60, seconds % 60);
    }

    /**
     * Appends one or more parameter-value pairs to a provided {@code uri}. Removes a query parameter if the
     * corresponding value is {@code null}. Use this method to build a custom query string for a given URI.
     * <pre>
     * addQueryParameters("http://www.example.com/users.jsp","id","12345","type","9876") = http://www.example.com/users.jsp?id=12345&amp;type=9876
     * addQueryParameters("http://www.example.com/users.jsp?id=23","id",null)            = http://www.example.com/users.jsp
     * </pre>
     *
     * @param uri URI to which the key-value pairs are appended.
     * @param parameters Sequence of one or more key-value pairs. The key is typically a string, and the value can be
     * any object.
     * @return A URI with the appended parameter-value pairs. May return {@code null}.
     * @see #encodeUri(String)
     * @see #decodeUri(String)
     */
    public static String addQueryParameters(String uri, Object... parameters) {
        if (uri == null) {
            return null;
        }

        // remove anchor to append after new parameters get added
        int poundAt = uri.indexOf('#');
        String anchor = null;
        if (poundAt > -1) {
            anchor = uri.substring(poundAt);
            uri = uri.substring(0, poundAt);
        }

        // Convert "path?a=b&c=d" to "&a=b&c=d".
        StringBuilder query = new StringBuilder();
        int questionAt = uri.indexOf('?');
        if (questionAt > -1) {

            String queryString = uri.substring(questionAt + 1);
            int beginAt = 0;

            // make sure all the query parameters are encoded
            while (true) {
                int ampIndex = queryString.indexOf('&', beginAt);

                String param = queryString.substring(beginAt, ampIndex > -1 ? ampIndex : queryString.length());

                if (!param.isEmpty() || ampIndex > -1) {
                    query.append('&');

                    int equalsIndex = param.indexOf('=');
                    if (equalsIndex > -1) {
                        query.append(encodeUri(decodeUri(param.substring(0, equalsIndex))));
                        query.append('=');
                        query.append(encodeUri(decodeUri(param.substring(equalsIndex + 1))));

                    } else {
                        query.append(encodeUri(decodeUri(param)));
                    }
                }

                if (ampIndex > -1) {
                    beginAt = ampIndex + 1;
                } else {
                    break;
                }
            }

            uri = uri.substring(0, questionAt);
        }

        int parametersLength = parameters != null ? parameters.length : 0;

        if (parameters == null) {
            return uri;
        }

        for (int i = 0; i < parametersLength; i += 2) {
            // Remove all occurrences of "&name=".
            String name = parameters[i].toString();
            String prefix = "&" + name + "=";
            int prefixLength = prefix.length();
            int beginAt = 0;
            int endAt;
            while (true) {

                beginAt = query.indexOf(prefix, beginAt);
                if (beginAt < 0) {
                    break;
                }

                endAt = query.indexOf("&", beginAt + prefixLength);
                if (endAt > -1) {
                    query.delete(beginAt, endAt);

                } else {
                    query.delete(beginAt, query.length());
                    break;
                }
            }

            // Append "&name=value".
            if (i + 1 < parametersLength) {
                Object value = parameters[i + 1];
                if (value != null) {
                    for (Object item : ObjectUtils.to(Iterable.class, value)) {
                        if (item != null) {
                            query.append('&');
                            query.append(encodeUri(name));
                            query.append('=');
                            query.append(encodeUri(item instanceof Enum
                                ? ((Enum<?>) item).name()
                                : item.toString()));
                        }
                    }
                }
            }
        }

        // Reconstruct the URI.
        if (query.length() <= 1) {
            return uri;

        } else {
            query.delete(0, 1);
            query.insert(0, "?");
            query.insert(0, uri);

            // ensure anchor follows any query parameters
            if (anchor != null) {
                query.append(anchor);
            }

            return query.toString();
        }
    }

    /**
     * Encodes the given UTF-8 {@code string} so that it's safe for use within a URI.
     * <pre>
     * encodeUri("Have you seen 2+ aliens (yes/no)") = "Have%20you%20seen%202%2B%20aliens%20%28yes%2Fno%29"
     * </pre>
     *
     * @param string URI to encode, typically a path that appears after http://www.domain.com/.
     * @return URI-safe string. May return {@code null}.
     * @throws IllegalStateException if cannot encode {@code string} into a string using the UTF-8 character set.
     */
    public static String encodeUri(String string) {
        if (string == null) {
            return null;
        }
        try {
            return URLEncoder.encode(string, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Decodes the given URI-encoded, UTF-8 {@code string}.
     * <pre>
     * decodeUri("Have%20you%20seen%202%2B%20aliens%20%28yes%2Fno%29") = "Have you seen 2+ aliens (yes/no)"
     * </pre>
     *
     * @param string URI to decode, typically a path that appears after http://www.domain.com/.
     * @return URI decoded string. May return {@code null}.
     * @throws IllegalStateException if cannot decode {@code string} into a string using the UTF-8 character set.
     */
    public static String decodeUri(String string) {
        if (string == null) {
            return null;
        }
        try {
            return URLDecoder.decode(string, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sleeps for the specified number of milliseconds.
     *
     * <p>Interrupts are handled by restoring the interrupt flag on the current thread.
     * This allows calling code to check for interrupts after the sleep completes.</p>
     *
     * <p>TODO: Consider using a more sophisticated sleep mechanism that can be cancelled</p>
     *
     * @param millis the number of milliseconds to sleep
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Performs an unchecked cast from Object to a generic type.
     *
     * <p>This is a convenience method for cases where you know the type is correct
     * but Java's type system requires an explicit cast. Returns {@code null} if
     * the object is null.</p>
     *
     * <p>FIXME: Unchecked casts bypass type safety - use with caution and only when you're certain of the type</p>
     *
     * @param <T> the target type
     * @param object the object to cast
     * @return the cast object, or {@code null} if object is null
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> T uncheckedCast(Object object) {
        return object == null ? null : (T) object;
    }
}
