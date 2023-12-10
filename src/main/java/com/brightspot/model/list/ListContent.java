package com.brightspot.model.list;

import java.util.List;

import com.psddev.cms.db.Site;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
public abstract class ListContent extends Record {

    public abstract List<?> getItems(Site site);

    public abstract Integer getItemsPerPage();
}
