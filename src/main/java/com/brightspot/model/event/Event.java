package com.brightspot.model.event;

import java.util.Date;
import java.util.Optional;

import com.brightspot.model.expiry.Expirable;
import com.brightspot.model.list.sort.alphabetical.AlphabeticalSortable;
import com.brightspot.model.list.sort.analytics.PageViewsSortable;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.PageMainViewModel;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.slug.Sluggable;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.brightspot.utils.RichTextUtils;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@ToolUi.DefaultSortField("startDate")
@ToolUi.FieldDisplayOrder({
    "name",
    "displayName",
    "sluggable.slug"
})
@ViewBinding(value = EventPageViewModel.class, types = PageMainViewModel.MAIN_CONTENT_VIEW)
public class Event extends AbstractPage implements
    AlphabeticalSortable,
    Expirable,
    PageViewsSortable,
    Promotable,
    Sluggable {

    private static final String PROMOTABLE_TYPE = "event";

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
            .map(Utils::toNormalized)
            .map(prefix -> StringUtils.appendIfMissing(prefix, "/"))
            .map(prefix -> prefix + asSluggableData().getSlug())
            .orElse(asSluggableData().getSlug());
    }

    // Expirable

    @Override
    public Boolean isExpired() {
        Date now = new Date();
        Date expiryDate = ObjectUtils.firstNonBlank(endDate, startDate, now);

        return !now.before(expiryDate);
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

    @Override
    public String getPromotableType() {
        return PROMOTABLE_TYPE;
    }

    // Sluggable

    @Override
    public String getSlugFallback() {
        return Utils.toNormalized(getDisplayName());
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
