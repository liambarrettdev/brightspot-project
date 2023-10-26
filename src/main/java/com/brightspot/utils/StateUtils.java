package com.brightspot.utils;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.State;
import com.psddev.dari.db.StateSerializer;
import com.psddev.dari.util.ObjectUtils;

public final class StateUtils {

    private StateUtils() {
    }

    public <S, T> T resolveAndGet(S record, Function<S, T> getter) {
        boolean addListener = State.getInstance(record).isResolveToReferenceOnly();
        UnresolvedStateLazyLoader listener = null;

        if (addListener) {
            listener = new UnresolvedStateLazyLoader();
            State.Static.addListener(listener);
        }

        try {
            return getter.apply(record);
        } finally {
            if (addListener) {
                State.Static.removeListener(listener);
            }
        }
    }

    public static <T> T resolve(T object) {
        if (object == null) {
            return null;
        }

        State state = State.getInstance(object);
        if (state.isReferenceOnly()) {
            return Utils.uncheckedCast(Query.fromAll().where("_id = ?", state.getId()).noCache().first());
        } else {
            return object;
        }
    }

    static class UnresolvedStateLazyLoader extends State.Listener {

        @Override
        public void beforeFieldGet(State state, String name) {
            Object value = state.getRawValue(name);
            if (value instanceof Map && ((Map<?, ?>) value).containsKey(StateSerializer.REFERENCE_KEY)) {
                UUID id = ObjectUtils.to(UUID.class, ((Map<?, ?>) value).get(StateSerializer.REFERENCE_KEY));
                Object obj = Query.findById(Record.class, id);
                state.put(name, obj);
            } else if (value instanceof Record && ((Record) value).getState().isReferenceOnly()) {
                UUID id = ((Record) value).getId();
                Object obj = Query.findById(Record.class, id);
                state.put(name, obj);
            }
        }
    }
}
