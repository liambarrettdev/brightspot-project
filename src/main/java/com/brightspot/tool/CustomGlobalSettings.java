package com.brightspot.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.psddev.cms.db.Localization;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.CmsTool;
import com.psddev.dari.db.Application;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.BeanProperty("global")
@Recordable.FieldInternalNamePrefix("settings.global.")
public class CustomGlobalSettings extends Modification<CmsTool> {

    public static final String TAB_DEFAULTS = "Defaults";
    public static final String TAB_DEBUG = "Debug";

    protected static final String DEFAULT_EMAIL = "noreply@brightspot.com";

    @ToolUi.Tab(TAB_DEFAULTS)
    @ToolUi.DisplayFirst
    @ToolUi.Placeholder(DEFAULT_EMAIL)
    private String defaultEmail;

    @ToolUi.Heading("Email")

    @ToolUi.Tab(TAB_DEBUG)
    @ToolUi.NoteHtml("<span data-dynamic-html='${content.global.getDisableEmailDeliveryNote()}'></span>")
    private Boolean disableEmailDelivery;

    @ToolUi.Tab(TAB_DEBUG)
    @ToolUi.Note("The list of addresses to which leads/emails will be redirected when delivery is disabled")
    private List<String> fallbackEmailAddresses;

    public String getDefaultEmail() {
        return ObjectUtils.firstNonBlank(defaultEmail, DEFAULT_EMAIL);
    }

    public void setDefaultEmail(String defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    public Boolean isDisableEmailDelivery() {
        return Boolean.TRUE.equals(disableEmailDelivery);
    }

    public void setDisableEmailDelivery(Boolean disableEmailDelivery) {
        this.disableEmailDelivery = disableEmailDelivery;
    }

    public List<String> getFallbackEmailAddresses() {
        if (fallbackEmailAddresses == null) {
            fallbackEmailAddresses = new ArrayList<>();
        }
        return fallbackEmailAddresses;
    }

    public void setFallbackEmailAddresses(List<String> fallbackEmailAddresses) {
        this.fallbackEmailAddresses = fallbackEmailAddresses;
    }

    // -- Helper Methods -- //

    public String getFallbackEmail() {
        return String.join(",", getFallbackEmailAddresses());
    }

    public String getDisableEmailDeliveryNote() {
        StringBuilder note = new StringBuilder();
        if (isDisableEmailDelivery()) {
            note.append("<div class='message message-warning'>");
            if (getFallbackEmailAddresses().isEmpty()) {
                String message = Localization.currentUserText(CustomGlobalSettings.class, "email.disabled");
                note.append(message);
            } else {
                String message = Localization.currentUserText(CustomGlobalSettings.class, "email.redirected");
                note.append(String.format(message, getFallbackEmail()));
            }
            note.append("</div>");
        } else {
            String message = Localization.currentUserText(CustomGlobalSettings.class, "email.note");
            note.append("<div class='message message-info'>").append(message).append("</div>");
        }
        return note.toString();
    }

    // -- Static Methods --//

    public static CustomGlobalSettings getInstance() {
        return Application.Static.getInstance(CmsTool.class).as(CustomGlobalSettings.class);
    }

    public static <T> T get(Function<CustomGlobalSettings, T> getter) {
        return get(getter, null);
    }

    public static <T> T get(Function<CustomGlobalSettings, T> getter, T defaultValue) {
        CustomGlobalSettings settings = getInstance();
        if (settings != null) {
            T value = getter.apply(settings);
            if (!ObjectUtils.isBlank(value)) {
                return value;
            }
        }
        return defaultValue;
    }
}
