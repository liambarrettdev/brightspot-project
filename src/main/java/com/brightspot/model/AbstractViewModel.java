package com.brightspot.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.user.User;
import com.brightspot.tool.field.annotation.CurrentUser;
import com.brightspot.tool.rte.RichTextProcessor;
import com.brightspot.project.view.base.util.ConcatenatedView;
import com.brightspot.project.view.base.util.LinkView;
import com.brightspot.project.view.base.util.RawHtmlView;
import com.brightspot.project.view.model.promo.PromoView;
import com.brightspot.project.view.model.promo.list.ListView;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.cms.view.ViewModel;
import com.psddev.cms.view.servlet.CurrentSite;
import com.psddev.cms.view.servlet.MainObject;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractViewModel<M> extends ViewModel<M> {

    @CurrentUser
    protected User user;

    @CurrentSite
    protected Site site;

    @MainObject
    protected Content mainContent;

    public User getUser() {
        return user;
    }

    public Site getSite() {
        return site;
    }

    public Content getMainContent() {
        return mainContent;
    }

    // -- Helper Methods --//

    protected ConcatenatedView buildRichTextView(String richText) {
        if (StringUtils.isBlank(richText)) {
            return null;
        }

        return new ConcatenatedView.Builder()
            .addAllToItems(RichTextProcessor
                .createDefault(richText)
                .renderUnhandledRichTextElements(false)
                .htmlViewFunction((String html) -> new RawHtmlView.Builder().html(html).build())
                .richTextElementViewFunction(rte -> createView(RichTextProcessor.RICH_TEXT_ELEMENT_VIEW_TYPE, rte))
                .build())
            .build();
    }

    protected Object buildModuleView(AbstractModule module) {
        return Optional.ofNullable(module)
            .map(m -> createView(m.getViewType(), m.unwrap()))
            .orElse(null);
    }

    protected ListView buildPromoListView(List<? extends Promotable> items) {
        return buildPromoListView(null, null, items);
    }

    protected ListView buildPromoListView(Object title, String description, List<? extends Promotable> items) {
        if (ObjectUtils.isBlank(items)) {
            return null;
        }

        return new ListView.Builder()
            .title(title)
            .description(description)
            .items(items.stream().map(item -> createView(PromoView.class, item)).collect(Collectors.toList()))
            .build();
    }

    protected LinkView buildLinkView(String url, String text) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        return new LinkView.Builder()
            .href(url)
            .body(text)
            .build();
    }
}
