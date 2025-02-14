package com.brightspot.model.pagination;

import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.ObjectUtils;

@ViewBinding(value = PaginationViewModel.class)
public class Pagination {

    public static final String PARAM_PAGE = "p";

    private Long totalItemCount;

    private Integer pageCountLimit;

    public Pagination(long totalItemCount, int pageCountLimit) {
        this.totalItemCount = totalItemCount;
        this.pageCountLimit = pageCountLimit;
    }

    public Long getTotalItemCount() {
        return ObjectUtils.firstNonBlank(totalItemCount, 0L);
    }

    public void setTotalItemCount(Long totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public Integer getPageCountLimit() {
        return ObjectUtils.firstNonBlank(pageCountLimit, 0);
    }

    public void setPageCountLimit(Integer pageCountLimit) {
        this.pageCountLimit = pageCountLimit;
    }

    public long getPageCount() {
        return (long) Math.ceil((double) getTotalItemCount() / getPageCountLimit());
    }
}
