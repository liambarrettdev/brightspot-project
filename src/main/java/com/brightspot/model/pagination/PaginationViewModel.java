package com.brightspot.model.pagination;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.pagination.PaginationView;
import com.psddev.dari.util.StringUtils;

public class PaginationViewModel extends AbstractViewModel<Pagination> implements PaginationView {

    @Override
    protected boolean shouldCreate() {
        return model.getPageCount() > 1;
    }

    @Override
    public Number getPageNumber() {
        return getAdjustedPageNumber();
    }

    @Override
    public Number getPageCount() {
        return model.getPageCount();
    }

    @Override
    public Object getNextPage() {
        long nextPageNumber = getAdjustedPageNumber() + 1;

        return nextPageNumber <= model.getPageCount()
            ? StringUtils.addQueryParameters(baseUrl, Pagination.PARAM_PAGE, nextPageNumber)
            : null;
    }

    @Override
    public Object getPreviousPage() {
        long previousPageNumber = getAdjustedPageNumber() - 1;

        return previousPageNumber > 0
            ? StringUtils.addQueryParameters(baseUrl, Pagination.PARAM_PAGE, previousPageNumber)
            : null;
    }

    private long getAdjustedPageNumber() {
        return pageNumber < 1
            ? 1
            : (pageNumber < model.getPageCount() ? pageNumber : model.getPageCount());
    }
}
