package com.brightspot.model.crosslinker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.brightspot.model.link.ExternalLink;
import com.brightspot.model.link.InternalLink;
import com.brightspot.model.link.Link;
import com.brightspot.model.link.Linkable;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.crosslinker.db.Term;
import com.psddev.crosslinker.db.TermMapping;
import com.psddev.crosslinker.db.Word;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.PageContextFilter;

public class LinkTermMapping extends TermMapping {

    @Recordable.Required
    private Link link;

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    // -- Overrides -- //

    @Override
    public void transform(Term term, Word word) {
        getLinkUrl().ifPresent(url -> {
            List<Object> attributes = new ArrayList<>();

            // add class
            attributes.add("class");
            attributes.add("crosslinker-link");

            // add href
            attributes.add("href");
            attributes.add(url);

            // add target
            Optional.ofNullable(getLink())
                .map(Link::getTarget)
                .map(Link.Target::getValue)
                .ifPresent(target -> {
                    attributes.add("target");
                    attributes.add(target);
                });

            word.wrapWithElement("a", attributes.toArray(new Object[0]));
        });
    }

    @Override
    public void update(Map<String, Object> map) {
        super.updateDictionary(map);

        Linkable content = Query.from(Linkable.class).where("id = ?", map.get("objectId")).first();
        if (content != null) {
            InternalLink link = new InternalLink();
            link.setItem(content);

            setLink(link);
        } else {
            ExternalLink link = new ExternalLink();
            link.setUrl(map.getOrDefault("url", "").toString());

            setLink(link);
        }
    }

    @Override
    public Set<Object> getIdentityObjects() {
        Set<Object> identities = new HashSet<>();
        getLinkUrl().ifPresent(identities::add);
        return identities;
    }

    // -- Utility Methods -- //

    private Optional<String> getLinkUrl() {
        Site site = Optional.ofNullable(PageContextFilter.Static.getRequestOrNull())
            .map(PageFilter.Static::getSite)
            .orElse(null);

        return Optional.ofNullable(getLink()).map(link -> link.getLinkUrl(site));
    }
}
