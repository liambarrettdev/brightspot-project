package com.brightspot.model.page.content;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.util.CollectionUtils;
import com.brightspot.model.link.ExternalLink;
import com.brightspot.model.link.InternalLink;
import com.brightspot.model.link.Link;
import com.brightspot.model.link.Linkable;
import com.brightspot.model.page.AbstractPageViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.base.util.LinkView;

public class AbstractContentPageViewModel extends AbstractPageViewModel<AbstractContentPage> {

    @Override
    public Collection<?> getBreadcrumbs() {
        List<Link> breadcrumbs = model.getBreadcrumbs().stream()
            .filter(Linkable.class::isInstance)
            .map(Linkable.class::cast)
            .map(InternalLink::create)
            .collect(Collectors.toList());

        // check if breadcrumbs configured for this content type
        if (CollectionUtils.isNullOrEmpty(breadcrumbs)) {
            return null;
        }

        // add current page as read-only node
        Optional.of(getMainContent())
            .filter(Linkable.class::isInstance)
            .map(Linkable.class::cast)
            .map(Linkable::getLinkableText)
            .map(text -> ExternalLink.create(text, null))
            .ifPresent(breadcrumbs::add);

        return breadcrumbs.stream()
            .map(breadcrumb -> createView(LinkView.class, breadcrumb))
            .collect(Collectors.toList());
    }

    @Override
    public Object getLead() {
        return createView(ImageView.class, model.getLeadImage());
    }

    @Override
    public Object getHeadline() {
        return buildRichTextView(model.getHeadline());
    }

    @Override
    public Object getSubHeadline() {
        return buildRichTextView(model.getSubHeadline());
    }
}
