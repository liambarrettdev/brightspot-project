package com.brightspot.model.blog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.view.model.blog.BlogPageView;
import com.brightspot.view.model.pagination.PaginationView;
import com.brightspot.view.model.promo.PromoModuleView;
import com.psddev.cms.db.Content;
import com.psddev.cms.view.ViewResponse;
import com.psddev.cms.view.servlet.HttpParameter;
import com.psddev.cms.view.servlet.HttpServletPath;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.StringUtils;

public class BlogPageViewModel extends AbstractViewModel<Blog> implements BlogPageView {

    protected static final String PARAM_PAGE = "p";

    @HttpServletPath
    protected String baseUrl;

    @HttpParameter(PARAM_PAGE)
    private long pageNumber;

    private transient long pageCount;
    private transient List<BlogPost> posts = new ArrayList<>();

    @Override
    protected void onCreate(ViewResponse response) {
        super.onCreate(response);

        Query<BlogPost> query = Query.from(BlogPost.class)
            .where(Hierarchical.Data.PARENT_FIELD + " = ?", model)
            .and(Content.PUBLISH_DATE_FIELD + " != missing");

        long totalCount = query.count();
        int limit = model.getPostsPerPage();

        pageCount = (long) Math.ceil((double) totalCount / limit);
        pageNumber = pageNumber < 1 ? 1 : (pageNumber < pageCount ? pageNumber : pageCount);

        posts = query.sortDescending(Content.PUBLISH_DATE_FIELD)
            .select((pageNumber - 1) * limit, limit)
            .getItems();
    }

    @Override
    public Collection<?> getPosts() {
        return posts.stream()
            .map(post -> createView(PromoModuleView.class, post))
            .collect(Collectors.toList());
    }

    @Override
    public Object getPagination() {
        return new PaginationView.Builder()
            .pageNumber(pageNumber)
            .pageCount(pageCount)
            .nextPage(getNextPage())
            .previousPage(getPreviousPage())
            .build();
    }

    private String getNextPage() {
        long nextPageNumber = pageNumber + 1;

        return nextPageNumber <= pageCount
            ? StringUtils.addQueryParameters(baseUrl, PARAM_PAGE, nextPageNumber)
            : null;
    }

    private String getPreviousPage() {
        long previousPageNumber = pageNumber - 1;

        return previousPageNumber > 0
            ? StringUtils.addQueryParameters(baseUrl, PARAM_PAGE, previousPageNumber)
            : null;
    }
}
