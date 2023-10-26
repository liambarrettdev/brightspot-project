package com.brightspot.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import com.ibm.icu.text.NumberFormat;
import com.brightspot.tool.CustomSiteSettings;
import com.psddev.cms.db.Localization;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUser;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

public final class LocalizationUtils {

    public static final String DEFAULT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz";

    private LocalizationUtils() {
    }

    public static String currentSiteText(Record record, String key) {
        return currentSiteText(record, key, null);
    }

    public static String currentSiteText(Record record, String key, String defaultValue) {
        Site site = record.as(Site.ObjectModification.class).getOwner();

        return currentSiteText(record, site, key, defaultValue);
    }

    public static String currentSiteText(Recordable record, Site site, String key, String defaultValue) {
        Locale locale = CustomSiteSettings.get(site, CustomSiteSettings::getLocale);

        return Localization.text(locale, record, key, defaultValue);
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

        String timezone = Optional.ofNullable(Utils.getCurrentToolUser())
            .map(ToolUser::getTimeZone)
            .orElse(ZoneId.systemDefault().toString());

        DateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        return formatter.format(date);
    }

    public static String localizeNumber(Number number, Locale locale) {
        if (number == null) {
            return null;
        }

        NumberFormat numberFormat = NumberFormat.getInstance(locale == null ? Locale.getDefault() : locale);

        return numberFormat.format(number);
    }

    public static String localizeCurrency(Number number, Locale locale) {
        if (number == null) {
            return null;
        }

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale == null ? Locale.getDefault() : locale);
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(number);
    }

    public static Locale getLocaleOrDefault(Locale locale) {
        return locale != null ? locale : Locale.getDefault();
    }
}
