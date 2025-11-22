package com.brightspot.model.person;

import java.util.List;
import java.util.Optional;

import com.brightspot.model.image.Image;
import com.brightspot.model.link.Linkable;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.model.page.PageViewModel;
import com.brightspot.model.promo.Promotable;
import com.brightspot.tool.HasImagePreview;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.brightspot.utils.Utils;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.ElFunctionUtils;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.StringUtils;

@Content.PreviewField("getPreviewStorageItem")
@ViewBinding(value = PersonPageViewModel.class, types = PageViewModel.MAIN_CONTENT_VIEW)
public class Person extends AbstractPage implements
    HasImagePreview,
    Linkable,
    Promotable {

    private static final String PROMOTABLE_TYPE = "person";
    private static final String DEFAULT_THUMBNAIL = "/assets/placeholders/person.png";

    @Recordable.Indexed(unique = true)
    private String email;

    private String position;

    private String website;

    private String shortBiography;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class, inline = false)
    private String biography;

    @Recordable.Embedded
    @ToolUi.NoteHtml("<span data-dynamic-html='${content.getAvatarPlaceholderHtml()}'></span>")
    private Image avatar;

    @Recordable.Indexed
    @ToolUi.Tab("Content")
    public List<HasAuthor> getMostRecentContent() {
        return Query.from(HasAuthor.class)
            .where(HasAuthor.Data.AUTHOR_FIELD + " = ?", this)
            .sortDescending(Content.PUBLISH_DATE_FIELD)
            .resolveToReferenceOnly()
            .select(0, 10)
            .getItems();
    }

    @Recordable.Indexed
    @ToolUi.Hidden
    public StorageItem getPreviewStorageItem() {
        return Optional.ofNullable(getPreviewImage())
            .map(Image::getFile)
            .orElse(null);
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

    public String getShortBiography() {
        return shortBiography;
    }

    public void setShortBiography(String shortBiography) {
        this.shortBiography = shortBiography;
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

    public String getNameFallback() {
        StringBuilder builder = new StringBuilder(getDisplayName());
        if (!StringUtils.isBlank(position)) {
            builder.append(String.format(" (%s)", position));
        }
        return builder.toString();
    }

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

    // Promotable

    @Override
    public String getPromoTitleFallback() {
        return getDisplayName();
    }

    @Override
    public String getPromoDescriptionFallback() {
        return getShortBiography();
    }

    @Override
    public Image getPromoImageFallback() {
        return getAvatar();
    }

    @Override
    public String getPromotableType() {
        return PROMOTABLE_TYPE;
    }

    // -- Helper Methods -- //

    public String getAvatarPlaceholderHtml() {
        return ObjectUtils.isBlank(avatar)
            ? writePreviewImageHtml(getPreviewImage())
            : null;
    }

    protected Image getPreviewImage() {
        return ObjectUtils.isBlank(avatar)
            ? Image.createImage(getAudioThumbnailFallback())
            : avatar;
    }

    protected StorageItem getAudioThumbnailFallback() {
        String url = ElFunctionUtils.resource(DEFAULT_THUMBNAIL);
        return StorageItem.Static.createUrl(url);
    }
}
