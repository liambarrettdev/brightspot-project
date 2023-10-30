package com.brightspot.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.brightspot.model.page.HeadScript;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

@Recordable.BeanProperty("integration")
@Recordable.FieldInternalNamePrefix("settings.integration.")
public class IntegrationSiteSettings extends Modification<Site> {

    public static final String TAB_INTEGRATIONS = "Integrations";

    @ToolUi.Tab(TAB_INTEGRATIONS)
    @Embedded
    private List<HeadScript> extraHeadScripts;

    @ToolUi.Tab(TAB_INTEGRATIONS)
    @Embedded
    private List<TagManager> tagManagers;

    public List<HeadScript> getExtraHeadScripts() {
        if (extraHeadScripts == null) {
            extraHeadScripts = new ArrayList<>();
        }
        return extraHeadScripts;
    }

    public void setExtraHeadScripts(List<HeadScript> extraHeadScripts) {
        this.extraHeadScripts = extraHeadScripts;
    }

    public List<TagManager> getTagManagers() {
        if (tagManagers == null) {
            tagManagers = new ArrayList<>();
        }
        return tagManagers;
    }

    public void setTagManagers(List<TagManager> tagManagers) {
        this.tagManagers = tagManagers;
    }

    // -- Static Methods --//

    public static <T> T get(Site site, Function<IntegrationSiteSettings, T> getter) {
        return site == null
            ? null
            : getter.apply(site.as(IntegrationSiteSettings.class));
    }
}
