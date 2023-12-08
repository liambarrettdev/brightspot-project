package com.brightspot.model.list.modifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.brightspot.utils.Utils;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.State;

public interface DynamicListModifier extends Recordable {

    void updateQuery(Query<?> query);

    static void updateQueryWithModifications(Recordable dynamicList, Query<?> query) {
        for (Class<? extends DynamicListModifier> modifier : getModifiers(dynamicList)) {
            dynamicList.as(modifier).updateQuery(query);
        }
    }

    static Collection<Class<? extends DynamicListModifier>> getModifiers(Recordable dynamicList) {
        Collection<Class<? extends DynamicListModifier>> modifiers = new ArrayList<>();

        Set<String> modificationNames = Optional.ofNullable(dynamicList)
            .map(Recordable::getState)
            .map(State::getType)
            .map(ObjectType::getModificationClassNames)
            .orElse(new HashSet<>());

        for (String modificationName : modificationNames) {
            Class<?> modification = Optional.ofNullable(ObjectType.getInstance(modificationName))
                .map(ObjectType::getObjectClass)
                .orElse(null);

            if (modification != null && DynamicListModifier.class.isAssignableFrom(modification)) {
                Class<? extends DynamicListModifier> modifier = Utils.uncheckedCast(modification);
                modifiers.add(modifier);
            }
        }

        return modifiers;
    }
}
