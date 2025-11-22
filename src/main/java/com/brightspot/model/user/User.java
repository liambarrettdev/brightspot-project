package com.brightspot.model.user;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.brightspot.auth.AuthenticationUser;
import com.brightspot.model.image.Image;
import com.brightspot.tool.DefaultGlobal;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Recordable;

@ToolUi.FieldDisplayOrder({
    "email",
    "password.password",
    "status",
    "avatar",
    "firstName",
    "lastName",
    "biography"
})
@ToolUi.Publishable(false)
public class User extends Content implements
    AuthenticationUser,
    DefaultGlobal,
    PasswordUser {

    @ToolUi.Heading("Basic Details")

    @Recordable.Indexed(unique = true)
    @Recordable.Required
    private String email;

    @ToolUi.Placeholder("Pending")
    private Status status = Status.PENDING;

    @ToolUi.Heading("Personal Details")

    @Recordable.Embedded
    private Image avatar;

    @Recordable.Indexed
    @ToolUi.CssClass("is-one-half")
    private String firstName;

    @Recordable.Indexed
    @ToolUi.CssClass("is-one-half")
    private String lastName;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class, inline = false)
    private String biography;

    @Recordable.Indexed
    @ToolUi.Hidden
    public String getFullName() {
        return Stream.of(getFirstName(), getLastName())
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" "));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    // -- Enums -- //

    public enum Status {
        BLOCKED("Blocked"),
        PENDING("Pending"),
        VERIFIED("Verified");

        private final String name;

        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
