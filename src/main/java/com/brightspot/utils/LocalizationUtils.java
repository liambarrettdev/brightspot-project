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

public final class LocalizationUtils {

    public static final String DEFAULT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz";

    private LocalizationUtils() {
    }

    public static String currentSiteText(Recordable record, String key) {
        return currentSiteText(record, key, null);
    }

    public static String currentSiteText(Recordable record, String key, String defaultValue) {
        Site site = record.as(Site.ObjectModification.class).getOwner();

        return currentSiteText(record, site, key, defaultValue);
    }

    public static String currentSiteText(Object object, Site site, String key, String defaultValue) {
        Locale locale = CustomSiteSettings.get(site, CustomSiteSettings::getLocale);

        return Localization.text(locale, object, key, defaultValue);
    }

    public static String currentUserDate(Date date) {
        return currentUserDate(date, null);
    }

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

    public static String localizeDate(Date date, Site site, String format) {
        Locale locale = CustomSiteSettings.get(site, CustomSiteSettings::getLocale);

        return localizeDate(date, locale, format);
    }

    public static String localizeDate(Date date, Locale locale, String format) {
        if (date == null) {
            return null;
        }

        return DateTimeFormatter.ofPattern(format)
            .withLocale(getLocaleOrDefault(locale))
            .format(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    public static String localizeNumber(Number number, Site site) {
        Locale locale = CustomSiteSettings.get(site, CustomSiteSettings::getLocale);

        return localizeNumber(number, locale);
    }

    public static String localizeNumber(Number number, Locale locale) {
        if (number == null) {
            return null;
        }

        NumberFormat numberFormat = NumberFormat.getInstance(getLocaleOrDefault(locale));

        return numberFormat.format(number);
    }

    public static String localizeCurrency(Number number, Site site) {
        Locale locale = CustomSiteSettings.get(site, CustomSiteSettings::getLocale);

        return localizeCurrency(number, locale);
    }

    public static String localizeCurrency(Number number, Locale locale) {
        if (number == null) {
            return null;
        }

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(getLocaleOrDefault(locale));
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(number);
    }

    public static Locale getLocaleOrDefault(Locale locale) {
        return locale != null ? locale : Locale.getDefault();
    }
}
