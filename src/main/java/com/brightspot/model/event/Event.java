package com.brightspot.model.event;

import java.util.Date;
import java.util.Optional;

import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.AbstractPageViewModel;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.slug.Sluggable;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.brightspot.utils.RichTextUtils;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.StringUtils;

@ToolUi.DefaultSortField("startDate")
@ToolUi.FieldDisplayOrder({
    "name",
    "displayName",
    "sluggable.slug"
})
@ViewBinding(value = EventPageViewModel.class, types = AbstractPageViewModel.MAIN_CONTENT_VIEW)
public class Event extends AbstractPage implements
    Promotable,
    Sluggable {

    @Required
    @ToolUi.CssClass("is-half")
    private String location;

    @Indexed
    @Required
    @ToolUi.Filterable
    @ToolUi.CssClass("is-half")
    private Type type = Type.ONLINE;

    @Indexed
    @Required
    @ToolUi.Filterable
    @ToolUi.CssClass("is-half")
    private Date startDate;

    @Indexed
    @ToolUi.Filterable
    @ToolUi.CssClass("is-half")
    private Date endDate;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String description;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        if (StringUtils.isBlank(asSluggableData().getSlug())) {
            return null;
        }

        return Optional.ofNullable(getType())
            .map(Type::toString)
            .map(StringUtils::toNormalized)
            .map(prefix -> StringUtils.ensureEnd(prefix, "/"))
            .map(prefix -> prefix + asSluggableData().getSlug())
            .orElse(asSluggableData().getSlug());
    }

    // Linkable

    @Override
    public String getLinkableText() {
        return getDisplayName();
    }

    // Promotable

    @Override
    public String getPromoTitleFallback() {
        return getDisplayName();
    }

    @Override
    public String getPromoDescriptionFallback() {
        return Optional.ofNullable(getDescription())
            .map(RichTextUtils::stripRichTextElements)
            .map(RichTextUtils::getFirstBodyParagraph)
            .map(RichTextUtils::richTextToPlainText)
            .orElse(null);
    }

    // Sluggable

    @Override
    public String getSlugFallback() {
        return StringUtils.toNormalized(getDisplayName());
    }

    // -- Enums -- //

    public enum Type {
        IN_PERSON("In Person"),
        ONLINE("Online");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
