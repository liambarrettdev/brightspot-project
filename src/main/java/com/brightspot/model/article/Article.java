package com.brightspot.model.article;

import com.brightspot.model.bookmark.Bookmarkable;
import com.brightspot.model.page.AbstractPageViewModel;
import com.brightspot.model.page.creativework.AbstractCreativeWorkPage;
import com.brightspot.model.rte.RichTextModule;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.crosslinker.db.Crosslinkable;

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
@ViewBinding(value = ArticlePageViewModel.class, types = { AbstractPageViewModel.MAIN_CONTENT_VIEW })
public class Article extends AbstractCreativeWorkPage implements
    Bookmarkable,
    Crosslinkable {

    @Required
    @Crosslinkable.Crosslinked
    private RichTextModule body = new RichTextModule();

    public RichTextModule getBody() {
        return body;
    }

    public void setBody(RichTextModule body) {
        this.body = body;
    }
}
