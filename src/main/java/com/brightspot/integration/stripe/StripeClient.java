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

    public Customer getCustomer(String customerId) throws StripeOperationException {
        if (StringUtils.isBlank(customerId)) {
            throw new IllegalArgumentException("Customer ID cannot be blank");
        }

        try {
            return Customer.retrieve(customerId);
        } catch (StripeException e) {
            LOGGER.error("Could not find Customer with ID {}", customerId, e);
            throw new StripeOperationException(String.format("Failed to retrieve customer with ID: %s", customerId), e
            );
        }
    }

    public Customer updateCustomer(Customer customer, Map<String, Object> invoiceSettings)
        throws StripeOperationException {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put(PARAM_INVOICE_SETTINGS, invoiceSettings);

            return customer.update(params);
        } catch (StripeException e) {
            LOGGER.error("Could not update Customer with ID {}", customer.getId(), e);
            throw new StripeOperationException(
                String.format("Failed to update customer with ID: %s", customer.getId()),
                e
            );
        }
    }

    public PaymentMethod createPaymentMethod(Map<String, Object> card, Map<String, Object> billingDetails)
        throws StripeOperationException {
        if (card == null) {
            throw new IllegalArgumentException("Card details cannot be null");
        }

        if (billingDetails == null) {
            throw new IllegalArgumentException("Billing details cannot be null");
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put(PARAM_TYPE, PARAM_CARD);
            params.put(PARAM_CARD, card);
            params.put(PARAM_BILLING_DETAILS, billingDetails);

            return PaymentMethod.create(params);
        } catch (StripeException e) {
            LOGGER.error("Could not create PaymentMethod", e);
            throw new StripeOperationException("Failed to create payment method", e);
        }
    }

    public PaymentMethod getPaymentMethod(String paymentMethodId) throws StripeOperationException {
        if (StringUtils.isBlank(paymentMethodId)) {
            throw new IllegalArgumentException("Payment method ID cannot be blank");
        }

        try {
            return PaymentMethod.retrieve(paymentMethodId);
        } catch (StripeException e) {
            LOGGER.error("Could not find Payment Method with ID {}", paymentMethodId, e);
            throw new StripeOperationException(String.format("Failed to retrieve payment method with ID: %s", paymentMethodId), e);
        }
    }

    public List<PaymentMethod> getPaymentMethods(Customer customer) throws StripeOperationException {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put(PARAM_TYPE, PARAM_CARD);
            params.put(PARAM_CUSTOMER, customer.getId());

            PaymentMethodCollection customerPaymentMethod = PaymentMethod.list(params);

            return new ArrayList<>(customerPaymentMethod.getData());
        } catch (StripeException e) {
            LOGGER.error("Could not find PaymentMethods for customer with ID {}", customer.getId(), e);
            throw new StripeOperationException(String.format("Failed to retrieve payment methods for customer with ID: %s", customer.getId()), e);
        }
    }

    public PaymentMethod attachPaymentMethod(Customer customer, PaymentMethod paymentMethod)
        throws StripeOperationException {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put(PARAM_CUSTOMER, customer.getId());

            return paymentMethod.attach(params);
        } catch (StripeException e) {
            LOGGER.error("Could not attach PaymentMethod {} to Customer {}", paymentMethod.getId(), customer.getId(), e);
            throw new StripeOperationException(String.format("Failed to attach payment method %s to customer %s", paymentMethod.getId(), customer.getId()), e);
        }
    }

    public PaymentMethod detachPaymentMethod(PaymentMethod paymentMethod) throws StripeOperationException {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        try {
            return paymentMethod.detach();
        } catch (StripeException e) {
            LOGGER.error("Could not detach PaymentMethod {}", paymentMethod.getId(), e);
            throw new StripeOperationException(String.format("Failed to detach payment method: %s", paymentMethod.getId()), e);
        }
    }

    public PaymentIntent getPaymentIntent(String intentId) throws StripeOperationException {
        if (StringUtils.isBlank(intentId)) {
            throw new IllegalArgumentException("Payment intent ID cannot be blank");
        }

        try {
            return PaymentIntent.retrieve(intentId);
        } catch (StripeException e) {
            LOGGER.error("Could not find PaymentIntent with ID {}", intentId, e);
            throw new StripeOperationException(String.format("Failed to retrieve payment intent with ID: %s", intentId), e);
        }
    }

    public PaymentIntent createPaymentIntent(Map<String, Object> params) throws StripeOperationException {
        if (params == null) {
            throw new IllegalArgumentException("Payment intent parameters cannot be null");
        }

        try {
            return PaymentIntent.create(params);
        } catch (StripeException e) {
            LOGGER.error("Could not create PaymentIntent", e);
            throw new StripeOperationException("Failed to create payment intent", e);
        }
    }

    public PaymentIntent updatePaymentIntent(PaymentIntent paymentIntent, Map<String, Object> params)
        throws StripeOperationException {
        if (paymentIntent == null) {
            throw new IllegalArgumentException("Payment intent cannot be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("Update parameters cannot be null");
        }

        try {
            return paymentIntent.update(params);
        } catch (StripeException e) {
            LOGGER.error("Could not update PaymentIntent {}", paymentIntent.getId(), e);
            throw new StripeOperationException(String.format("Failed to update payment intent: %s", paymentIntent.getId()), e);
        }
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
