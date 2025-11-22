package com.brightspot.model.list.sort;

import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;

public interface DynamicListSort extends Recordable {

    void updateQuery(Query<?> query);
}
