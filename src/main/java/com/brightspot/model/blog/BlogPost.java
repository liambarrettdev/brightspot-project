package com.brightspot.model.blog;

import java.util.Optional;

import com.brightspot.model.bookmark.Bookmarkable;
import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.page.PageViewModel;
import com.brightspot.model.page.creativework.AbstractCreativeWorkPage;
import com.brightspot.model.rte.RichTextModule;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.crosslinker.db.Crosslinkable;
import org.apache.commons.lang3.StringUtils;

@ToolUi.FieldDisplayOrder({
    "name",
    "displayName",
    "update",
    "sluggable.slug",
    "authorable.author",
    "leadImage",
    "headline",
    "subHeadline",
    "body",
    "blog",
    "categorizable.category",
    "taggable.tags"
})
@Crosslinkable.SimulationName("Default")
@ViewBinding(value = BlogPostPageViewModel.class, types = PageViewModel.MAIN_CONTENT_VIEW)
public class BlogPost extends AbstractCreativeWorkPage implements
    Bookmarkable,
    Crosslinkable {

    private static final String PROMOTABLE_TYPE = "blog-post";

    @Required
    @Crosslinkable.Crosslinked
    private RichTextModule body = new RichTextModule();

    @Required
    private Blog blog;

    public RichTextModule getBody() {
        return body;
    }

    public void setBody(RichTextModule body) {
        this.body = body;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        String slug = asSlugData().getSlug();
        if (StringUtils.isBlank(slug)) {
            return null;
        }

        return StringUtils.appendIfMissing(StringUtils.prependIfMissing(Optional.ofNullable(getBlog())
            .map(s -> s.createPermalink(site))
            .orElse(""), "/"), "/") + slug;
    }

    // Hierarchical

    @Override
    public Hierarchical getHierarchicalParent() {
        return getBlog();
    }

    // Promotable

    @Override
    public String getPromotableType() {
        return PROMOTABLE_TYPE;
    }
}
