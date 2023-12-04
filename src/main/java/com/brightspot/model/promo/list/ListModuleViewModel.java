package com.brightspot.model.promo.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.pagination.Pagination;
import com.brightspot.view.model.pagination.PaginationView;
import com.brightspot.view.model.promo.PromoModuleView;
import com.brightspot.view.model.promo.list.ListModuleView;
import com.psddev.cms.view.ViewResponse;
import org.apache.commons.collections4.ListUtils;

public class ListModuleViewModel extends AbstractViewModel<ListModule> implements ListModuleView {

    private transient Pagination pagination;
    private transient List<?> items = new ArrayList<>();

    @Override
    protected boolean shouldCreate() {
        return model.getSupplier() != null && !model.getSupplier().getItems(getCurrentSite()).isEmpty();
    }

    @Override
    protected void onCreate(ViewResponse response) {
        super.onCreate(response);

        List<?> totalItems = model.getSupplier().getItems(getCurrentSite());

        long totalCount = totalItems.size();
        int limit = model.getSupplier().getItemsPerPage();

        int pageCount = (int) Math.ceil((double) totalCount / limit);
        int currentPageNumber = pageNumber < 1 ? 1 : (pageNumber < pageCount ? pageNumber : pageCount);

        pagination = new Pagination(totalCount, limit);
        items = ListUtils.partition(totalItems, limit).get(currentPageNumber - 1);
    }

    @Override
    public Object getTitle() {
        return model.getTitle();
    }

    @Override
    public Object getDescription() {
        return buildRichTextView(model.getDescription());
    }

    @Override
    public Collection<?> getItems() {
        return items.stream()
            .map(item -> createView(PromoModuleView.class, item))
            .collect(Collectors.toList());
    }

    @Override
    public Object getPagination() {
        return createView(PaginationView.class, pagination);
    }
}
