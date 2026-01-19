package com.brightspot.utils;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.State;
import com.psddev.dari.db.StateSerializer;
import com.psddev.dari.util.ObjectUtils;

/**
 * Utility class for working with Dari State objects.
 *
 * <p>This class provides methods to resolve lazy-loaded references and work with
 * State objects that may be reference-only (not fully loaded from the database).</p>
 *
 * <p>These utilities are particularly useful when working with objects that may
 * be partially loaded or when you need to ensure references are resolved before
 * accessing their properties.</p>
 */
public final class StateUtils {

    private StateUtils() {
    }

    /**
     * Resolves lazy-loaded references and gets a value from a record.
     *
     * <p>If the record's state is configured to resolve to reference only, this method
     * adds a listener that automatically resolves references when fields are accessed.
     * The getter function is then applied and the result is returned.</p>
     *
     * <p>This is useful when you need to access a property on a record that may have
     * unresolved references that need to be loaded from the database.</p>
     *
     * @param <S> the record type
     * @param <T> the return type
     * @param record the record to get the value from
     * @param getter the function to apply to get the value
     * @return the value returned by the getter function
     */
    public static <S, T> T resolveAndGet(S record, Function<S, T> getter) {
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

    /**
     * Resolves a reference-only object to its full database representation.
     *
     * <p>If the object's state is reference-only (not fully loaded), queries the
     * database to load the full object. Returns the original object if it's already
     * fully loaded or if the object is null.</p>
     *
     * <p>TODO: Consider adding caching to avoid repeated database queries for the same object</p>
     *
     * @param <T> the object type
     * @param object the object to resolve (may be reference-only)
     * @return the fully resolved object, or the original object if already resolved or null
     */
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

    /**
     * Listener that automatically resolves unresolved state references when fields are accessed.
     *
     * <p>This listener is used internally by {@link #resolveAndGet} to automatically
     * load references from the database when they are accessed on reference-only states.</p>
     */
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
