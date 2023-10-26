package com.brightspot.utils;

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

    public static Site getCurrentSite() {
        ToolUser user = getCurrentToolUser();
        return user != null ? user.getCurrentSite() : null;
    }

    public static ToolUser getCurrentToolUser() {
        HttpServletRequest request = PageContextFilter.Static.getRequestOrNull();
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
