package com.brightspot.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import com.brightspot.tool.CustomSiteSettings;
import com.ibm.icu.text.NumberFormat;
import com.psddev.cms.db.Localization;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUser;
import com.psddev.dari.db.Recordable;

/**
 * Utility class for localization and internationalization operations.
 *
 * <p>This class provides methods for localizing text, dates, numbers, and currency
 * based on site settings and user preferences. It integrates with the Brightspot
 * localization system and ICU number formatting.</p>
 */
public final class LocalizationUtils {

    public static final String DEFAULT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz";

    private LocalizationUtils() {
    }

    /**
     * Gets localized text for the current site.
     *
     * <p>Retrieves the site owner from the record and uses it to get localized text.
     * Returns {@code null} if no localized text is found.</p>
     *
     * @param record the recordable object to get the site from
     * @param key the localization key
     * @return the localized text, or {@code null} if not found
     */
    public static String currentSiteText(Recordable record, String key) {
        return currentSiteText(record, key, null);
    }

    /**
     * Gets localized text for the current site with a default value.
     *
     * <p>Retrieves the site owner from the record and uses it to get localized text.
     * Returns the default value if no localized text is found.</p>
     *
     * @param record the recordable object to get the site from
     * @param key the localization key
     * @param defaultValue the default value if localization is not found
     * @return the localized text, or the default value if not found
     */
    public static String currentSiteText(Recordable record, String key, String defaultValue) {
        Site site = record.as(Site.ObjectModification.class).getOwner();

        return currentSiteText(record, site, key, defaultValue);
    }

    public static String currentSiteText(Object object, Site site, String key, String defaultValue) {
        Locale locale = CustomSiteSettings.get(site, CustomSiteSettings::getLocale);

        return Localization.text(locale, object, key, defaultValue);
    }

    /**
     * Formats a date using the current user's timezone and default format.
     *
     * <p>Uses the timezone from the current tool user's settings, or falls back to
     * the system default timezone. Uses the default date format pattern.</p>
     *
     * @param date the date to format
     * @return the formatted date string, or {@code null} if date is null
     */
    public static String currentUserDate(Date date) {
        return currentUserDate(date, null);
    }

    /**
     * Formats a date using the current user's timezone and a custom format pattern.
     *
     * <p>Uses the timezone from the current tool user's settings, or falls back to
     * the system default timezone. Uses the provided pattern, or the default pattern
     * if pattern is null.</p>
     *
     * @param date the date to format
     * @param pattern the date format pattern (uses default if null)
     * @return the formatted date string, or {@code null} if date is null
     */
    public static String currentUserDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        if (pattern == null) {
            pattern = DEFAULT_DATE_FORMAT;
        }

        ZoneId timezone = Optional.ofNullable(Utils.getCurrentToolUser())
            .map(ToolUser::getTimeZone)
            .map(ZoneId::of)
            .orElse(ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.toInstant().atZone(timezone).format(formatter);
    }

    /**
     * Localizes a date using a site's locale and format pattern.
     *
     * <p>Uses the locale from the site's custom settings and formats the date
     * using the provided format pattern. Uses the system default timezone.</p>
     *
     * @param date the date to localize
     * @param site the site to get the locale from
     * @param format the date format pattern
     * @return the localized date string, or {@code null} if date is null
     */
    public static String localizeDate(Date date, Site site, String format) {
        Locale locale = CustomSiteSettings.get(site, CustomSiteSettings::getLocale);

        return localizeDate(date, locale, format);
    }

    /**
     * Localizes a date using a locale and format pattern.
     *
     * <p>Formats the date using the provided locale and format pattern. Uses
     * the system default timezone.</p>
     *
     * <p>TODO: Consider using the site's timezone instead of system default</p>
     *
     * @param date the date to localize
     * @param locale the locale to use for formatting
     * @param format the date format pattern
     * @return the localized date string, or {@code null} if date is null
     */
    public static String localizeDate(Date date, Locale locale, String format) {
        if (date == null) {
            return null;
        }

        return DateTimeFormatter.ofPattern(format)
            .withLocale(getLocaleOrDefault(locale))
            .format(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    /**
     * Localizes a number using a site's locale.
     *
     * <p>Uses the locale from the site's custom settings to format the number
     * according to locale-specific number formatting rules.</p>
     *
     * @param number the number to localize
     * @param site the site to get the locale from
     * @return the localized number string, or {@code null} if number is null
     */
    public static String localizeNumber(Number number, Site site) {
        Locale locale = CustomSiteSettings.get(site, CustomSiteSettings::getLocale);

        return localizeNumber(number, locale);
    }

    /**
     * Localizes a number using a locale.
     *
     * <p>Formats the number according to locale-specific number formatting rules
     * (e.g., decimal separators, thousand separators).</p>
     *
     * @param number the number to localize
     * @param locale the locale to use for formatting
     * @return the localized number string, or {@code null} if number is null
     */
    public static String localizeNumber(Number number, Locale locale) {
        if (number == null) {
            return null;
        }

        NumberFormat numberFormat = NumberFormat.getInstance(getLocaleOrDefault(locale));

        return numberFormat.format(number);
    }

    /**
     * Localizes a number as currency using a site's locale.
     *
     * <p>Uses the locale from the site's custom settings to format the number
     * as currency with locale-specific currency symbols and formatting. The
     * maximum fraction digits is set to 0 (no decimal places).</p>
     *
     * @param number the number to format as currency
     * @param site the site to get the locale from
     * @return the localized currency string, or {@code null} if number is null
     */
    public static String localizeCurrency(Number number, Site site) {
        Locale locale = CustomSiteSettings.get(site, CustomSiteSettings::getLocale);

        return localizeCurrency(number, locale);
    }

    /**
     * Localizes a number as currency using a locale.
     *
     * <p>Formats the number as currency with locale-specific currency symbols and
     * formatting. The maximum fraction digits is set to 0 (no decimal places).</p>
     *
     * <p>TODO: Consider making the maximum fraction digits configurable</p>
     *
     * @param number the number to format as currency
     * @param locale the locale to use for formatting
     * @return the localized currency string, or {@code null} if number is null
     */
    public static String localizeCurrency(Number number, Locale locale) {
        if (number == null) {
            return null;
        }

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(getLocaleOrDefault(locale));
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(number);
    }

    /**
     * Gets a locale or returns the system default if null.
     *
     * <p>Convenience method to ensure a non-null locale is always returned.</p>
     *
     * @param locale the locale to check
     * @return the provided locale, or the system default locale if null
     */
    public static Locale getLocaleOrDefault(Locale locale) {
        return locale != null ? locale : Locale.getDefault();
    }
}
