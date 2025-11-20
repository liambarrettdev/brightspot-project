package com.brightspot.model.promo.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.pagination.Pagination;
import com.brightspot.model.promo.PromotableDelegateViewModel;
import com.brightspot.view.model.pagination.PaginationView;
import com.brightspot.view.model.promo.list.ListModuleView;
import com.psddev.cms.view.ViewResponse;
import org.apache.commons.collections4.ListUtils;

public class ListModuleViewModel extends AbstractViewModel<ListModule> implements ListModuleView {

    private transient Pagination pagination;
    private transient List<?> items = new ArrayList<>();

    @Override
    protected boolean shouldCreate() {
        return model.getContent() != null && !model.getContent().getItems(getCurrentSite()).isEmpty();
    }

    @Override
    protected void onCreate(ViewResponse response) {
        super.onCreate(response);

        List<?> totalItems = model.getContent().getItems(getCurrentSite());

        long totalCount = totalItems.size();
        int limit = model.getContent().getItemsPerPage();

        int pageCount = (int) Math.ceil((double) totalCount / limit);
        int modulePageNumber = getModuleSpecificPageNumber(model.getId());
        int currentPageNumber = modulePageNumber < 1 ? 1 : Math.min(modulePageNumber, pageCount);

        pagination = new Pagination(totalCount, limit, model.getId());
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
            .map(item -> createView(PromotableDelegateViewModel.class, item))
            .collect(Collectors.toList());
    }

    @Override
    public Object getPagination() {
        return createView(PaginationView.class, pagination);
    }

    @Override
    public Object getModuleId() {
        return model.getId().toString();
    }
}
