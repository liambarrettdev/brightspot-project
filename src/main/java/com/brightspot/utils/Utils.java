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

public final class Utils {

    private Utils() {
    }

    public static String toNormalized(CharSequence string) {
        return com.psddev.dari.util.StringUtils.toNormalized(string);
    }

    public static Site getCurrentSite() {
        ToolUser user = getCurrentToolUser();
        return user != null ? user.getCurrentSite() : null;
    }

    public static ToolUser getCurrentToolUser() {
        return getCurrentToolUser(PageContextFilter.Static.getRequestOrNull());
    }

    public static ToolUser getCurrentToolUser(HttpServletRequest request) {
        return request != null ? AuthenticationFilter.Static.getUser(request) : null;
    }

    public static <T extends Number> T numericValue(String input, Class<T> clazz) {
        if (StringUtils.isBlank(input)) {
            return null;
        }

        Number value = NumberUtils.createNumber(input.replaceAll("[^0-9]", ""));
        return ObjectUtils.to(clazz, value);
    }

    public static <T> T findMapEntry(String key, Map<String, Object> content, Class<T> clazz, T fallback) {
        return Optional.ofNullable(content)
            .map(m -> m.get(key))
            .map(e -> ObjectUtils.to(clazz, e))
            .orElse(fallback);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

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
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
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
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException ignored) {
            //Ignored
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static <T> T uncheckedCast(Object object) {
        return object == null ? null : (T) object;
    }
}
