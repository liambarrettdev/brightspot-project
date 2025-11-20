package com.brightspot.model.pagination;

import java.util.UUID;

import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.ObjectUtils;

@ViewBinding(value = PaginationViewModel.class)
public class Pagination {

    public static final String PARAM_PAGE = "p";

    private Long totalItemCount;

    private Integer pageCountLimit;

    private UUID moduleId;

    public Pagination(long totalItemCount, int pageCountLimit, UUID moduleId) {
        this.totalItemCount = totalItemCount;
        this.pageCountLimit = pageCountLimit;
        this.moduleId = moduleId;
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

    public UUID getModuleId() {
        return moduleId;
    }

    public void setModuleId(UUID moduleId) {
        this.moduleId = moduleId;
    }

    public long getPageCount() {
        return (long) Math.ceil((double) getTotalItemCount() / getPageCountLimit());
    }

    public static String getModulePageParam(UUID moduleId) {
        return moduleId == null
            ? PARAM_PAGE + "_default"
            : PARAM_PAGE + "_" + moduleId;
    }
}
