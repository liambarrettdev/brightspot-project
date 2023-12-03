package com.brightspot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.promo.Promotable;
import com.brightspot.model.user.User;
import com.brightspot.tool.ViewWrapper;
import com.brightspot.tool.field.annotation.CurrentUser;
import com.brightspot.tool.rte.RichTextProcessor;
import com.brightspot.view.base.util.ConcatenatedView;
import com.brightspot.view.base.util.LinkView;
import com.brightspot.view.base.util.RawHtmlView;
import com.brightspot.view.model.promo.PromoModuleView;
import com.brightspot.view.model.promo.list.ListModuleView;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Site;
import com.psddev.cms.view.ViewModel;
import com.psddev.cms.view.servlet.CurrentSite;
import com.psddev.cms.view.servlet.MainObject;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractViewModel<M> extends ViewModel<M> {

    public static final String DEFAULT_DATE_FORMAT = "MMMM d, yyyy hh:mm a";

    @MainObject
    protected Content mainContent;

    @CurrentSite
    protected Site currentSite;

    @CurrentUser
    protected User currentUser;

    public Content getMainContent() {
        return mainContent;
    }

    public Site getCurrentSite() {
        return currentSite;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // -- Helper Methods --//

    protected Object buildObjectView(ViewWrapper wrapper) {
        return Optional.ofNullable(wrapper)
            .map(o -> buildObjectView(o.getViewType(), o.unwrap()))
            .orElse(null);
    }

    protected Object buildObjectView(String type, Object object) {
        return Optional.ofNullable(object)
            .map(o -> createView(type, object))
            .orElse(null);
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

    protected ListModuleView buildListModuleView(List<? extends Recordable> items) {
        return buildListModuleView(null, null, items);
    }

    protected ListModuleView buildListModuleView(Object title, String description, List<? extends Recordable> items) {
        if (ObjectUtils.isBlank(items)) {
            return null;
        }

        return new ListModuleView.Builder()
            .title(title)
            .description(description)
            .items(items.stream()
                .filter(Promotable.class::isInstance)
                .map(item -> createView(PromoModuleView.class, item))
                .collect(Collectors.toList()))
            .build();
    }

    protected List<Object> buildModuleViews(List<AbstractModule> modules) {
        return Optional.ofNullable(modules)
            .map(List::stream)
            .map(stream -> stream.map(this::buildObjectView)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))
            .orElse(new ArrayList<>());
    }

    protected ConcatenatedView buildRichTextView(String richText) {
        if (StringUtils.isBlank(richText)) {
            return null;
        }

        List<Object> richTextItems = RichTextProcessor
            .createDefault(richText)
            .renderUnhandledRichTextElements(false)
            .htmlViewFunction((String html) -> new RawHtmlView.Builder().html(html).build())
            .richTextElementViewFunction(rte -> createView(RichTextProcessor.RICH_TEXT_ELEMENT_VIEW_TYPE, rte))
            .build();

        return buildConcatenatedView(richTextItems);
    }

    protected ConcatenatedView buildConcatenatedView(List<Object> contents) {
        return new ConcatenatedView.Builder()
            .addAllToItems(contents)
            .build();
    }
}
