package com.brightspot.model.taxonomy;

import com.brightspot.model.hierarchy.Hierarchical;
import com.psddev.cms.db.Taxon;
import com.psddev.dari.util.ObjectUtils;

public interface Taxonomy extends Hierarchical, Taxon {

    // -- Overrides -- //

    // Taxon

    @Override
    default boolean isRoot() {
        return ObjectUtils.isBlank(getHierarchicalParent());
    }
}
