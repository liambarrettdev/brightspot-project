package com.brightspot.model.hierarchy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.brightspot.utils.StateUtils;
import com.google.common.collect.Lists;
import com.psddev.dari.db.Recordable;

public interface Hierarchical extends Recordable {

    Hierarchical getHierarchicalParent();

    default List<Hierarchical> getBreadcrumbs() {
        return Optional.of(this)
            .map(Hierarchical::getHierarchy)
            .map(ArrayList::new)
            .map(Lists::reverse)
            .orElse(new ArrayList<>());
    }

    // -- Static Methods -- //

    static Set<Hierarchical> getHierarchy(Hierarchical hierarchical) {
        Set<Hierarchical> hierarchy = new LinkedHashSet<>();

        Optional.ofNullable(hierarchical)
            .map(Hierarchical::getHierarchicalParent)
            .map(StateUtils::resolve)
            .ifPresent(parent -> {
                hierarchy.add(parent);
                hierarchy.addAll(getHierarchy(parent));
            });

        return hierarchy;
    }
}
