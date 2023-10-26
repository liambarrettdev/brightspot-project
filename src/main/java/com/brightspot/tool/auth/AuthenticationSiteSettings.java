package com.brightspot.tool.auth;

import java.util.function.Function;

import com.brightspot.auth.AuthenticationSettings;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

@Recordable.BeanProperty("auth")
@Recordable.FieldInternalNamePrefix("settings.auth.")
public class AuthenticationSiteSettings extends Modification<Site> {

    @ToolUi.Tab("Authentication")
    private AuthenticationSettings settings;

    public AuthenticationSettings getSettings() {
        return settings;
    }

    public void setSettings(AuthenticationSettings settings) {
        this.settings = settings;
    }

    // -- Static Methods --//

    public static <T> T get(Site site, Function<AuthenticationSiteSettings, T> getter) {
        return site == null
            ? null
            : getter.apply(site.as(AuthenticationSiteSettings.class));
    }
}
