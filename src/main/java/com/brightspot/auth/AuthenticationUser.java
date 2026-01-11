package com.brightspot.auth;

import com.psddev.dari.db.Recordable;

/**
 * Marker interface for objects that can be authenticated.
 *
 * <p>Any {@link Recordable} object can implement this interface to participate in the
 * authentication system. The interface itself has no methods - it serves as a type marker
 * to identify objects that can be associated with authentication sessions.</p>
 *
 * <p>Common implementations include user models that extend from a base user class.</p>
 *
 * @see Recordable
 * @see Session
 * @see AbstractAuthenticator
 */
public interface AuthenticationUser extends Recordable {

}
