package com.brightspot.model.pagination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.LinkView;
import com.brightspot.view.model.pagination.PaginationView;
import com.psddev.cms.view.ViewResponse;
import com.psddev.dari.util.StringUtils;

public class PaginationViewModel extends AbstractViewModel<Pagination> implements PaginationView {

    private transient long adjustedPageNumber;

    @Override
    protected boolean shouldCreate() {
        return model.getPageCount() > 1;
    }

    @Override
    protected void onCreate(ViewResponse response) {
        adjustedPageNumber = calculateAdjustedPageNumber();
    }

    @Override
    public Collection<?> getLinks() {
        List<LinkView> links = new ArrayList<>();

        links.add(getPreviousPage());
        //TODO need logic to limit the number of links rendered
        for (int i = 1; i <= model.getPageCount(); i++) {
            links.add(createPageLink(i, null));
        }
        links.add(getNextPage());

        return links;
    }

    private LinkView getPreviousPage() {
        long previousPageNumber = this.adjustedPageNumber - 1;

        return previousPageNumber > 0
            ? createPageLink(previousPageNumber, "<")
            : null;
    }

    private LinkView getNextPage() {
        long nextPageNumber = this.adjustedPageNumber + 1;

        return nextPageNumber <= model.getPageCount()
            ? createPageLink(nextPageNumber, ">")
            : null;
    }

    private LinkView createPageLink(long pageNumber, String text) {
        String paramPage = Pagination.getModulePageParam(model.getModuleId());
        String url = StringUtils.addQueryParameters(baseUrl, paramPage, pageNumber);
        if (StringUtils.isBlank(url)) {
            return null;
        }

        return new LinkView.Builder()
            .href(url)
            .body(StringUtils.isBlank(text) ? pageNumber : text)
            .extraClass(pageNumber == this.adjustedPageNumber ? "active" : null)
            .build();
    }

    private long calculateAdjustedPageNumber() {
        int modulePageNumber = getModuleSpecificPageNumber(model.getModuleId());
        return modulePageNumber < 1
            ? 1
            : (modulePageNumber < model.getPageCount() ? modulePageNumber : model.getPageCount());
    }
}
