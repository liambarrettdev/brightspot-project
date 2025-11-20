package com.brightspot.model.article;

import java.util.Optional;

import com.brightspot.model.bookmark.Bookmarkable;
import com.brightspot.model.page.PageViewModel;
import com.brightspot.model.page.creativework.AbstractCreativeWorkPage;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.rte.RichTextModule;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.utils.RichTextUtils;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.crosslinker.db.Crosslinkable;
import com.psddev.dari.db.Recordable;

@ToolUi.FieldDisplayOrder({
    "name",
    "displayName",
    "sluggable.slug",
    "authorable.author",
    "leadImage",
    "headline",
    "subHeadline",
    "body",
    "categorizable.category",
    "taggable.tags"
})
@Crosslinkable.SimulationName("Default")
@ViewBinding(value = ArticlePageViewModel.class, types = { PageViewModel.MAIN_CONTENT_VIEW })
public class Article extends AbstractCreativeWorkPage implements
    Bookmarkable,
    Crosslinkable {

    private static final String PROMOTABLE_TYPE = "article";

    @Recordable.Required
    @Crosslinkable.Crosslinked
    private RichTextModule body = new RichTextModule();

    public RichTextModule getBody() {
        return body;
    }

    public void setBody(RichTextModule body) {
        this.body = body;
    }

    // -- Overrides -- //

    @Override
    public String getPromotableDuration(Site site) {
        long length = Optional.ofNullable(getBody())
            .map(RichTextModule::getRichText)
            .map(RichTextUtils::stripRichTextElements)
            .map(RichTextUtils::richTextToPlainText)
            .map(String::length)
            .map(Long::valueOf)
            .orElse(0L);

        long timeToRead = length / 1200;
        if (timeToRead < 1) {
            timeToRead = 1;
        }

        String durationLabel = LocalizationUtils.currentSiteText(Promotable.class, site, "duration", null);

        return String.format(durationLabel, timeToRead);
    }

    @Override
    public String getPromotableType() {
        return PROMOTABLE_TYPE;
    }
}
