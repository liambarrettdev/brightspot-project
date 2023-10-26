package com.brightspot.tool;

import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.ObjectIndex;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.Substitution;

public interface SiteSingleton extends Recordable {

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<SiteSingleton> {

        public static final String FIELD_PREFIX = "site.singleton.";
        public static final String KEY_FIELD = FIELD_PREFIX + "key";

        @Indexed(unique = true)
        @ToolUi.Hidden
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        // -- Overrides -- //

        @Override
        public void beforeSave() {
            Site site = this.as(Site.ObjectModification.class).getOwner();
            Class<?> nonSubstitutionClass = getNonSubstitutionSuperclass(getOriginalObject().getClass());
            key = nonSubstitutionClass.getName() + "/" + (site != null ? site.getId().toString() : "");
        }

        @Override
        protected boolean onDuplicate(ObjectIndex index) {
            if (index != null) {
                String field = index.getField();
                if (KEY_FIELD.equals(field)) {
                    getState().addError(getState().getField(KEY_FIELD), "Only one "
                        + getState().getType().getLabel() + " can be saved per Site!");
                }
            }

            return false;
        }

        // -- Utility Methods -- //

        private Class<?> getNonSubstitutionSuperclass(Class<?> currentClass) {
            while (Substitution.class.isAssignableFrom(currentClass)) {
                currentClass = currentClass.getSuperclass();
            }
            return currentClass;
        }
    }

    // -- Static Methods --//

    static <T extends SiteSingleton> T get(Class<T> singletonClass, Site siteOwner) {
        if (singletonClass != null) {
            String key = singletonClass.getName() + "/" + (siteOwner != null ? siteOwner.getId() : "");
            return Query.from(singletonClass).where(Data.KEY_FIELD + " = ?", key).first();
        }

        return null;
    }
}
