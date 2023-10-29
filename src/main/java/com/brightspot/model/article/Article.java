package com.brightspot.model.article;

import com.brightspot.model.page.AbstractPageViewModel;
import com.brightspot.model.page.content.AbstractContentPage;
import com.brightspot.model.person.Authorable;
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
    "taggable.tags"
})
@Crosslinkable.SimulationName("Default")
@ViewBinding(value = ArticlePageViewModel.class, types = { AbstractPageViewModel.MAIN_CONTENT_VIEW })
public class Article extends AbstractContentPage implements
    Authorable,
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
