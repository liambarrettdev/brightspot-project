package com.brightspot.model.person;

import java.util.List;

import com.brightspot.model.image.Image;
import com.brightspot.model.link.Linkable;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.AbstractPageViewModel;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Query;

@Content.PreviewField("avatar")
@ViewBinding(value = PersonPageViewModel.class, types = AbstractPageViewModel.MAIN_CONTENT_VIEW)
public class Person extends AbstractPage implements Linkable {

    @Indexed(unique = true)
    private String email;

    private String position;

    private String website;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class, inline = false)
    private String biography;

    @Embedded
    private Image avatar;

    @Indexed
    @ToolUi.Tab("Content")
    public List<HasAuthor> getMostRecentContent() {
        return Query.from(HasAuthor.class)
            .where(HasAuthor.Data.AUTHOR_FIELD + " = ?", this)
            .sortDescending(Content.PUBLISH_DATE_FIELD)
            .resolveToReferenceOnly()
            .select(0, 10)
            .getItems();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    // -- Overrides -- //

    // Directory.Item

    @Override
    public String createPermalink(Site site) {
        return Utils.toNormalized(getLabel());
    }

    // Linkable

    @Override
    public String getLinkableText() {
        return getDisplayName();
    }
}
