package com.brightspot.integration.stripe;

import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

public interface StripeContact extends Recordable {

    default Data asStripeContactData() {
        return as(Data.class);
    }

    @FieldInternalNamePrefix(Data.FIELD_PREFIX)
    class Data extends Modification<StripeContact> {

        public static final String FIELD_PREFIX = "stripe.";
        public static final String HEADING_NAME = "Stripe";

        @ToolUi.Heading(HEADING_NAME)

        @ToolUi.Note("Required for associating this client with a customer in Stripe")
        private String contactId;

        public String getContactId() {
            return contactId;
        }

        public void setContactId(String contactId) {
            this.contactId = contactId;
        }
    }
}
