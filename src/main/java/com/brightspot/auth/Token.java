package com.brightspot.auth;

import java.util.Date;

import com.brightspot.model.expiry.Expirable;
import com.psddev.dari.db.Record;
import org.apache.commons.lang.time.DateUtils;

public class Token extends Record implements Expirable {

    @Required
    private AuthenticationUser user;

    @Required
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

    @Override
    public Boolean shouldDeleteAfterExpiry() {
        return true;
    }

    public static Token createToken(AuthenticationUser user) {
        Token token = new Token();
        token.setUser(user);
        token.setExpiryDate(DateUtils.addDays(new Date(), 7));
        token.save();
        return token;
    }
}
