package com.brightspot.model.bookmark;

import java.util.Optional;
import java.util.UUID;

import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.State;

public interface Bookmarkable extends Recordable {

    default String getBookmarkableId() {
        return Optional.ofNullable(this.as(Recordable.class))
            .map(Recordable::getState)
            .map(State::getId)
            .map(UUID::toString)
            .orElse(null);
    }
}