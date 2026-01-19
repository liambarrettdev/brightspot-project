package com.brightspot.utils;

import com.google.common.base.Preconditions;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Workflow;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;

/**
 * Utility class for content state and publishing operations.
 *
 * <p>This class provides methods to determine the publishing state of content objects,
 * including whether content is being published for the first time or is currently published.</p>
 *
 * <p>All methods require non-null objects and will throw {@link NullPointerException}
 * if a null object is provided.</p>
 */
public final class ContentUtils {

    private ContentUtils() {
    }

    /**
     * Determines if this is the first time a content object is being published.
     *
     * <p>A content object is considered to be published for the first time if:</p>
     * <ul>
     *   <li>It does not already exist in the database in a published state, AND</li>
     *   <li>It is not currently in draft state, AND</li>
     *   <li>It is not currently trashed, AND</li>
     *   <li>It is not currently in a workflow state</li>
     * </ul>
     *
     * <p>This method queries the database to check if the object already exists in a
     * published state, which may have performance implications for frequently called code.</p>
     *
     * <p>TODO: Consider caching the result or optimizing the database query</p>
     * <p>FIXME: The method name suggests "first publish" but the logic checks if it's "about to be published" - consider renaming to {@code isAboutToBePublished()} for clarity</p>
     *
     * @param object the content object to check
     * @return {@code true} if this is the first publish, {@code false} otherwise
     * @throws NullPointerException if object is null
     */
    public static boolean isFirstPublish(Object object) {

        Preconditions.checkNotNull(object);

        State state = State.getInstance(object);

        Object existingObject = Query.fromAll().where("id = ?", state.getId()).noCache().first();

        State existingState = State.getInstance(existingObject);

        // this Object is already published if:
        //  - it already exists in the database
        //  - AND the database object is not in draft
        //  - AND the database object is not trashed
        //  - AND the database object is not in workflow
        boolean isAlreadyPublished = existingObject != null
            && !existingState.as(Content.ObjectModification.class).isDraft()
            && !existingState.as(Content.ObjectModification.class).isTrash()
            && existingState.as(Workflow.Data.class).getCurrentState() == null;

        // this Object is about to be published for the first time if:
        //  - this Object is not already published (above)
        //  - AND it is not in draft
        //  - AND it is not trashed
        //  - AND it is not in workflow
        return !isAlreadyPublished
            && !state.as(Content.ObjectModification.class).isDraft()
            && !state.as(Content.ObjectModification.class).isTrash()
            && state.as(Workflow.Data.class).getCurrentState() == null;
    }

    /**
     * Determines if a content object is currently in a published state.
     *
     * <p>A content object is considered published if:</p>
     * <ul>
     *   <li>It is not in draft state, AND</li>
     *   <li>It is not trashed, AND</li>
     *   <li>It is not in a workflow state</li>
     * </ul>
     *
     * <p>This method only checks the current state of the object, not whether it exists
     * in the database. Use {@link #isFirstPublish(Object)} to check if this is the first
     * time the object is being published.</p>
     *
     * @param object the content object to check
     * @return {@code true} if the object is in a published state, {@code false} otherwise
     * @throws NullPointerException if object is null
     */
    public static boolean isPublish(Object object) {

        Preconditions.checkNotNull(object);

        State state = State.getInstance(object);

        return !state.as(Content.ObjectModification.class).isDraft()
            && !state.as(Content.ObjectModification.class).isTrash()
            && state.as(Workflow.Data.class).getCurrentState() == null;
    }
}
