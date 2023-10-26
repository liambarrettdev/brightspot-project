package com.brightspot.auth;

import java.util.Date;

import com.brightspot.model.user.User;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;

public class Session extends Record {

    @Indexed
    @Required
    private AuthenticationUser user;

    @Indexed
    @Required
    private Date start;

    @Indexed
    private Date end;

    public AuthenticationUser getUser() {
        return user;
    }

    public void setUser(AuthenticationUser user) {
        this.user = user;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    // -- Static Methods -- //

    /**
     * Returns session by ID
     *
     * @param id the session ID
     * @return the {@link Session} or {@code null} if no session is found or session is expired
     */
    public static Session getSession(String id) {
        return Query.from(Session.class)
            .where("_id = ?", id)
            .and("end = missing || end > ?", new Date())
            .first();
    }

    /**
     * Returns session by user
     *
     * @param user the session user
     * @return the {@link Session} or {@code null} if no session is found or session is expired
     */
    public static Session getSession(User user) {
        return Query.from(Session.class)
            .where("user = ?", user)
            .and("end = missing || end > ?", new Date())
            .first();
    }
}
