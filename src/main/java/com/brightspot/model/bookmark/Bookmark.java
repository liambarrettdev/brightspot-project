package com.brightspot.model.bookmark;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.brightspot.model.user.User;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;

@ToolUi.Hidden
public class Bookmark extends Record {

    @Required
    @Indexed(unique = true)
    private String bookmarkId;

    @Required
    @Indexed
    private User user;

    @Required
    @Indexed
    private Bookmarkable content;

    public String getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(String bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bookmarkable getContent() {
        return content;
    }

    public void setContent(Bookmarkable content) {
        this.content = content;
    }

    @Override
    protected void beforeSave() {
        setBookmarkId(getUser().getId() + "_" + getContent().getBookmarkableId());
    }

    @Override
    public String getLabel() {
        return Stream.of(getContent(), getUser())
            .filter(Objects::nonNull)
            .filter(Record.class::isInstance)
            .map(Record.class::cast)
            .map(Record::getLabel)
            .collect(Collectors.joining(" - "));
    }
}
