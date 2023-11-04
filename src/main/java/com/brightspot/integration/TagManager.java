package com.brightspot.integration;

import com.brightspot.model.page.HeadItem;
import com.psddev.dari.db.Recordable;

public interface TagManager extends HeadItem, Recordable {

    String HEAD_VIEW_TYPE = "tag-manager-head";

    String BODY_VIEW_TYPE = "tag-manager-body";

}
