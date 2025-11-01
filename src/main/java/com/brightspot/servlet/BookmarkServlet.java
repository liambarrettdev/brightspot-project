package com.brightspot.servlet;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.auth.AuthenticationFilter;
import com.brightspot.model.bookmark.Bookmark;
import com.brightspot.model.bookmark.Bookmarkable;
import com.brightspot.model.user.User;
import com.brightspot.utils.DatabaseUtils;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.RoutingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RoutingFilter.Path(BookmarkServlet.SERVLET_PATH)
public class BookmarkServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkServlet.class);

    public static final String SERVLET_PATH = "/_api/bookmark";

    public static final String PARAM_CONTENT_ID = "id";

    // -- Overrides -- //

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // prepare response
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setStatus(HttpServletResponse.SC_OK);

        // find request params
        String contentId = request.getParameter(PARAM_CONTENT_ID);

        // validate request
        Site site = PageFilter.Static.getSite(request);
        User user = Optional.ofNullable(AuthenticationFilter.getAuthenticatedUser(request))
            .filter(User.class::isInstance)
            .map(User.class::cast)
            .orElse(null);

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Bookmarkable content = DatabaseUtils.findById(Bookmarkable.class, contentId);
        if (content == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // create bookmark
        Query<Bookmark> query = Query.from(Bookmark.class)
            .where("cms.site.owner = ?", site)
            .and("content = ?", content)
            .and("user = ?", user);

        if (query.hasMoreThan(0)) {
            // bookmark already exists, take no action
            return;
        }

        // create new bookmark if one does not already exist
        Bookmark bookmark = new Bookmark();

        bookmark.setUser(user);
        bookmark.setContent(content);
        bookmark.as(Site.ObjectModification.class).setOwner(site);

        bookmark.save();

        LOGGER.debug("Created new bookmark for user {} and content {}", user.getId(), content.getBookmarkableId());
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // prepare response
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setStatus(HttpServletResponse.SC_OK);

        // find request params
        String contentId = request.getParameter(PARAM_CONTENT_ID);

        // validate request
        Site site = PageFilter.Static.getSite(request);
        User user = Optional.ofNullable(AuthenticationFilter.getAuthenticatedUser(request))
            .filter(User.class::isInstance)
            .map(User.class::cast)
            .orElse(null);

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Bookmarkable content = DatabaseUtils.findById(Bookmarkable.class, contentId);
        if (content == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // delete bookmark
        Query.from(Bookmark.class)
            .where("cms.site.owner = ?", site)
            .and("content = ?", content)
            .and("user = ?", user)
            .deleteAll();

        LOGGER.debug("Deleted bookmark for user {} and content {}", user.getId(), content.getBookmarkableId());
    }
}
