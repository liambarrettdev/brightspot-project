package com.brightspot.integration.stripe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href=https://stripe.com/docs/api?lang=java>SDK Documentation</a>
 */
public final class StripeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeClient.class);

    public static final String PARAM_TYPE = "type";
    public static final String PARAM_CARD = "card";
    public static final String PARAM_CUSTOMER = "customer";
    public static final String PARAM_BILLING_DETAILS = "billing_details";
    public static final String PARAM_INVOICE_SETTINGS = "invoice_settings";

    public static final String PARAM_NUMBER = "number";
    public static final String PARAM_EXPIRY_MONTH = "exp_month";
    public static final String PARAM_EXPIRY_YEAR = "exp_year";
    public static final String PARAM_CVC = "cvc";

    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_RECIPIENT_EMAIL = "receipt_email";
    public static final String PARAM_CURRENCY = "currency";
    public static final String PARAM_PAYMENT_TYPE = "payment_method_types";
    public static final String PARAM_PAYMENT_DEFAULT = "default_payment_method";

    private static StripeClient api;

    private StripeClient() {
    }

    public Customer getCustomer(String customerId) {
        try {
            return Customer.retrieve(customerId);
        } catch (StripeException e) {
            LOGGER.error("Could not find Customer with ID " + customerId, e);
        }

        return null;
    }

    public Customer updateCustomer(Customer customer, Map<String, Object> invoiceSettings) {
        if (customer == null) {
            return null;
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put(PARAM_INVOICE_SETTINGS, invoiceSettings);

            return customer.update(params);
        } catch (StripeException e) {
            LOGGER.error("Could not update Customer", e);
        }

        return null;
    }

    public PaymentMethod createPaymentMethod(Map<String, Object> card, Map<String, Object> billingDetails) {

        try {
            Map<String, Object> params = new HashMap<>();
            params.put(PARAM_TYPE, PARAM_CARD);
            params.put(PARAM_CARD, card);
            params.put(PARAM_BILLING_DETAILS, billingDetails);

            return PaymentMethod.create(params);
        } catch (StripeException e) {
            LOGGER.error("Could not create PaymentMethod", e);
        }

        return null;
    }

    public PaymentMethod getPaymentMethod(String paymentMethodId) {
        try {
            return PaymentMethod.retrieve(paymentMethodId);
        } catch (StripeException e) {
            LOGGER.error("Could not find Payment Method with ID " + paymentMethodId, e);
        }

        return null;
    }

    public List<PaymentMethod> getPaymentMethods(Customer customer) {
        if (customer == null) {
            return new ArrayList<>();
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put(PARAM_TYPE, PARAM_CARD);
            params.put(PARAM_CUSTOMER, customer.getId());

            PaymentMethodCollection customerPaymentMethod = PaymentMethod.list(params);

            return new ArrayList<>(customerPaymentMethod.getData());
        } catch (StripeException e) {
            LOGGER.error("Could not find PaymentMethods for customer", e);
        }

        return new ArrayList<>();
    }

    public PaymentMethod attachPaymentMethod(Customer customer, PaymentMethod paymentMethod) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(PARAM_CUSTOMER, customer.getId());

            return paymentMethod.attach(params);
        } catch (StripeException e) {
            LOGGER.error("Could not attach PaymentMethod to a Customer", e);
        }

        return null;
    }

    public PaymentMethod detachPaymentMethod(PaymentMethod paymentMethod) {
        try {
            return paymentMethod.detach();
        } catch (StripeException e) {
            LOGGER.error("Could not detach PaymentMethod from a Customer", e);
        }

        return null;
    }

    public PaymentIntent getPaymentIntent(String intentId) {
        try {
            return PaymentIntent.retrieve(intentId);
        } catch (StripeException e) {
            LOGGER.error("Could not find PaymentIntent with ID " + intentId, e);
        }

        return null;
    }

    public PaymentIntent createPaymentIntent(Map<String, Object> params) {
        try {
            return PaymentIntent.create(params);
        } catch (StripeException e) {
            LOGGER.error("Could not create PaymentIntent", e);
        }

        return null;
    }

    public PaymentIntent updatePaymentIntent(PaymentIntent paymentIntent, Map<String, Object> params) {
        if (paymentIntent == null) {
            return null;
        }

        try {
            return paymentIntent.update(params);
        } catch (StripeException e) {
            LOGGER.error("Could not update PaymentIntent", e);
        }

        return null;
    }

    public static Boolean checkDefaultPaymentMethod(Customer customer, String paymentMethodId) {
        return Optional.ofNullable(customer)
                .map(Customer::getInvoiceSettings)
                .map(Customer.InvoiceSettings::getDefaultPaymentMethod)
                .map(defaultPaymentMethod -> defaultPaymentMethod.equalsIgnoreCase(paymentMethodId))
                .orElse(false);
    }

    public static synchronized StripeClient getInstance(String key) {
        if (!StringUtils.isBlank(key)) {
            if (api == null) {
                // first request to client, initialise context
                api = new StripeClient();
                initializeContext(key);
            } else if (!key.equals(Stripe.apiKey)) {
                // all subsequent requests, check to see if the configurable API key has changed
                initializeContext(key);
            }
        }
        return api;
    }

    private static void initializeContext(String key) {
        Stripe.apiKey = key;
    }
}
