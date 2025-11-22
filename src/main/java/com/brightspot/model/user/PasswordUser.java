package com.brightspot.model.user;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.Password;

public interface PasswordUser extends Recordable {

    default Data asPasswordUserData() {
        return this.as(Data.class);
    }

    // -- Modification Data -- //

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<PasswordUser> {

        public static final String FIELD_PREFIX = "password.";

        @ToolUi.FieldDisplayType("password")
        private String password;

        @Recordable.Indexed
        @ToolUi.Hidden
        private String resetPasswordToken;

        @ToolUi.Hidden
        private long resetPasswordTokenTime;

        public Password getPassword() {
            return Password.valueOf(password);
        }

        public void setPassword(Password password) {
            this.password = password.toString();
        }

        public String getResetPasswordToken() {
            return resetPasswordToken;
        }

        public void setResetPasswordToken(String resetPasswordToken) {
            this.resetPasswordToken = resetPasswordToken;
            this.resetPasswordTokenTime = resetPasswordToken == null ? 0L : System.currentTimeMillis();
        }

        public long getResetPasswordTokenTime() {
            return resetPasswordTokenTime;
        }

        public void setResetPasswordTokenTime(long resetPasswordTokenTime) {
            this.resetPasswordTokenTime = resetPasswordTokenTime;
        }

        // -- Helper Methods -- //

        public boolean hasResetPasswordToken() {
            long tokenExpiration = 24 * 60L * 60L * 1000L; // to milliseconds
            return resetPasswordToken != null && (resetPasswordTokenTime + tokenExpiration
                > System.currentTimeMillis());
        }
    }
}
