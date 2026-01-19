package com.brightspot.auth;

import java.util.Date;

import com.brightspot.model.expiry.Expirable;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import org.apache.commons.lang.time.DateUtils;

/**
 * Represents a temporary authentication token used for email verification during user registration.
 *
 * <p>Tokens are created when a user registers and are used to verify their email address.
 * Each token is associated with a user and has an expiration date (default: 7 days).</p>
 *
 * <p>Tokens implement the {@link Expirable} interface and are automatically deleted after
 * expiration. They are single-use tokens that are deleted after successful verification.</p>
 *
 * <p>Tokens are typically sent to users via email with a verification link that includes
 * the token ID as a parameter.</p>
 *
 * @see Expirable
 * @see AuthenticationUser
 */
public class Token extends Record implements Expirable {

    @Recordable.Required
    private AuthenticationUser user;

    @Recordable.Required
    private Date expiryDate;

    public AuthenticationUser getUser() {
        return user;
    }

    public void setUser(AuthenticationUser user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Indicates that tokens should be automatically deleted after expiration.
     *
     * @return {@code true} to enable automatic deletion
     */
    @Override
    public Boolean shouldDeleteAfterExpiry() {
        return true;
    }

    /**
     * Creates a new token for a user with a 7-day expiration period.
     *
     * <p>The token is immediately saved to the database and can be used for email verification.
     * The token will be automatically deleted after 7 days if not used.</p>
     *
     * @param user the user to create a token for
     * @return the newly created token
     */
    public static Token createToken(AuthenticationUser user) {
        Token token = new Token();
        token.setUser(user);
        token.setExpiryDate(DateUtils.addDays(new Date(), 7));
        token.save();
        return token;
    }
}
