package com.brightspot.model.blog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.pagination.Pagination;
import com.brightspot.model.promo.PromotableDelegateViewModel;
import com.brightspot.view.model.blog.BlogPageView;
import com.brightspot.view.model.pagination.PaginationView;
import com.psddev.cms.db.Content;
import com.psddev.cms.view.ViewResponse;
import com.psddev.dari.db.Query;

public class BlogPageViewModel extends AbstractViewModel<Blog> implements BlogPageView {

    private transient Pagination pagination;
    private transient List<BlogPost> posts = new ArrayList<>();

    @Override
    protected void onCreate(ViewResponse response) {
        super.onCreate(response);

        Query<BlogPost> query = Query.from(BlogPost.class)
            .where(Hierarchical.Data.PARENT_FIELD + " = ?", model)
            .and(Content.PUBLISH_DATE_FIELD + " != missing");

        long totalCount = query.count();
        int limit = model.getPostsPerPage();

        int pageCount = (int) Math.ceil((double) totalCount / limit);
        long currentPageNumber = pageNumber < 1 ? 1 : (pageNumber < pageCount ? pageNumber : pageCount);

        pagination = new Pagination(totalCount, limit, model.getId());
        posts = query.sortDescending(Content.PUBLISH_DATE_FIELD)
            .select((currentPageNumber - 1) * limit, limit)
            .getItems();
    }

    @Override
    public Collection<?> getPosts() {
        return posts.stream()
            .map(post -> createView(PromotableDelegateViewModel.class, post))
            .collect(Collectors.toList());
    }

    @Override
    public Object getPagination() {
        return createView(PaginationView.class, pagination);
    }
}
