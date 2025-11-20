package com.brightspot.auth;

import java.util.Date;

import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import org.apache.commons.lang.time.DateUtils;

public class Session extends Record {

    @Recordable.Indexed
    @Recordable.Required
    private AuthenticationUser user;

    @Recordable.Indexed
    @Recordable.Required
    private Date start;

    @Recordable.Indexed
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
     * Creates a session for a user
     *
     * @param user the user to create a session for
     * @return the {@link Session} or {@code null} if no uer is provided
     */
    public static Session createSession(AuthenticationUser user) {
        if (user == null) {
            return null;
        }

        Session session = new Session();

        session.setUser(user);
        session.setStart(new Date());
        session.setEnd(DateUtils.addMonths(new Date(), 1));
        session.saveImmediately();

        return session;
    }

    /**
     * Returns session by ID
     *
     * @param id the session ID
     * @return the {@link Session} or {@code null} if no session is found or session is expired
     */
    public static Session findSession(String id) {
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
    public static Session findSession(AuthenticationUser user) {
        return Query.from(Session.class)
            .where("user = ?", user)
            .and("end = missing || end > ?", new Date())
            .first();
    }
}
