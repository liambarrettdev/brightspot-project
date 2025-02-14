package com.brightspot.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.integration.IntegrationSiteSettings;
import com.brightspot.integration.stripe.StripeClient;
import com.brightspot.integration.stripe.StripeContact;
import com.brightspot.integration.stripe.StripeSettings;
import com.brightspot.utils.DatabaseUtils;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.dari.util.IoUtils;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.gson.JsonArray;
import com.psddev.dari.util.gson.JsonObject;
import com.psddev.dari.util.gson.JsonParser;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles requests to create/update payment details in Stripe
 */
@RoutingFilter.Path(StripeServlet.SERVLET_PATH)
public class StripeServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeServlet.class);

    public static final String SERVLET_PATH = "/_api/stripe/";

    public static final String PARAM_ACTION = "action";
    public static final String PARAM_CARD_ID = "card_id";
    public static final String PARAM_CONTACT_ID = "contact_id";
    public static final String PARAM_INTENT_ID = "intent_id";
    public static final String PARAM_NUMBER = "number";
    public static final String PARAM_CVC = "cvc";
    public static final String PARAM_EXPIRY_MONTH = "expiry_month";
    public static final String PARAM_EXPIRY_YEAR = "expiry_year";

    public static final String JSON_ID = "id";
    public static final String JSON_TYPE = "type";
    public static final String JSON_NUMBER = "number";
    public static final String JSON_CARDS = "cards";
    public static final String JSON_CARD_NUMBER = "cardNumber";
    public static final String JSON_KEY = "publishableKey";
    public static final String JSON_SECRET = "clientSecret";

    public static final String KEY_CONTACT_EMAIL = "contactEmail";
    public static final String KEY_INTENT_AMOUNT = "intentAmount";

    public static final String ACTION_PAYMENT_METHOD = "paymentMethod";
    public static final String ACTION_PAYMENT_INTENT = "paymentIntent";

    private static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setStatus(HttpServletResponse.SC_OK);

        String action = request.getParameter(PARAM_ACTION);
        if (StringUtils.isNotBlank(action)) {
            try {
                Site site = PageFilter.Static.getSite(request);

                JsonObject jsonResponse;
                switch (action) {
                    case ACTION_PAYMENT_METHOD:
                        jsonResponse = retrievePaymentMethods(request, site);
                        break;
                    case ACTION_PAYMENT_INTENT:
                        throw new UnsupportedOperationException("Retrieving payment intents is not supported");
                    default:
                        jsonResponse = new JsonObject();
                }

                response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(jsonResponse.toString());
            } catch (Exception e) {
                LOGGER.error("Error executing Stripe request", e);

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setStatus(HttpServletResponse.SC_OK);

        String action = request.getParameter(PARAM_ACTION);
        if (StringUtils.isNotBlank(action)) {
            try {
                Site site = PageFilter.Static.getSite(request);

                JsonObject jsonResponse;
                switch (action) {
                    case ACTION_PAYMENT_METHOD:
                        jsonResponse = createPaymentMethod(request, site);
                        break;
                    case ACTION_PAYMENT_INTENT:
                        jsonResponse = createPaymentIntent(request, site);
                        break;
                    default:
                        jsonResponse = new JsonObject();
                }

                response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(jsonResponse.toString());
            } catch (Exception e) {
                LOGGER.error("Error executing Stripe request", e);

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setStatus(HttpServletResponse.SC_OK);

        String action = request.getParameter(PARAM_ACTION);
        if (StringUtils.isNotBlank(action)) {
            try {
                Site site = PageFilter.Static.getSite(request);

                JsonObject jsonResponse;
                switch (action) {
                    case ACTION_PAYMENT_METHOD:
                        jsonResponse = updatePaymentMethod(request, site);
                        break;
                    case ACTION_PAYMENT_INTENT:
                        jsonResponse = updatePaymentIntent(request, site);
                        break;
                    default:
                        jsonResponse = new JsonObject();
                }

                response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(jsonResponse.toString());
            } catch (Exception e) {
                LOGGER.error("Error executing Stripe request", e);

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, private, max-age=0, must-revalidate");
        response.setStatus(HttpServletResponse.SC_OK);

        String action = request.getParameter(PARAM_ACTION);
        if (StringUtils.isNotBlank(action)) {
            try {
                Site site = PageFilter.Static.getSite(request);

                JsonObject jsonResponse;
                switch (action) {
                    case ACTION_PAYMENT_METHOD:
                        jsonResponse = deletePaymentMethod(request, site);
                        break;
                    case ACTION_PAYMENT_INTENT:
                        throw new UnsupportedOperationException("Deleting payment intents is not supported");
                    default:
                        jsonResponse = new JsonObject();
                }

                response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(jsonResponse.toString());
            } catch (Exception e) {
                LOGGER.error("Error executing Stripe request", e);

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    protected JsonObject createPaymentMethod(HttpServletRequest request, Site site) throws Exception {
        StripeClient client = StripeClient.getInstance(getStripeSecret(site));
        if (client == null) {
            throw new ServletException("Stripe is not configured");
        }

        Customer customer = findCustomer(client, request);
        if (customer == null) {
            throw new ServletException("Unexpected error while retrieving customer data");
        }

        Map<String, Object> params = new HashMap<>();
        params.put(StripeClient.PARAM_NUMBER, request.getParameter(PARAM_NUMBER));
        params.put(StripeClient.PARAM_EXPIRY_MONTH, request.getParameter(PARAM_EXPIRY_MONTH));
        params.put(StripeClient.PARAM_EXPIRY_YEAR, request.getParameter(PARAM_EXPIRY_YEAR));
        params.put(StripeClient.PARAM_CVC, request.getParameter(PARAM_CVC));

        if (validatePaymentMethod(client, customer, params)) {
            throw new ServletException("Duplicate payment method");
        }

        PaymentMethod createdPaymentMethod = client.createPaymentMethod(params, new HashMap<>());
        if (createdPaymentMethod == null) {
            throw new ServletException("Unexpected error while creating new payment method");
        }

        PaymentMethod attachedPaymentMethod = client.attachPaymentMethod(customer, createdPaymentMethod);
        if (attachedPaymentMethod == null) {
            throw new ServletException("Unexpected error while attaching payment method to customer");
        }

        JsonObject responseJson = new JsonObject();

        responseJson.add(JSON_KEY, JSON_PARSER.parse(getStripeKey(site)));
        responseJson.add(JSON_CARD_NUMBER, JSON_PARSER.parse(attachedPaymentMethod.getCard().getLast4()));

        return responseJson;
    }

    protected JsonObject deletePaymentMethod(HttpServletRequest request, Site site) throws Exception {
        StripeClient client = StripeClient.getInstance(getStripeSecret(site));
        if (client == null) {
            throw new ServletException("Stripe is not configured");
        }

        PaymentMethod attachedPaymentMethod = client.getPaymentMethod(request.getParameter(PARAM_CARD_ID));
        if (attachedPaymentMethod == null) {
            throw new ServletException("Unexpected error while retrieving payment method data");
        }

        PaymentMethod detachedPaymentMethod = client.detachPaymentMethod(attachedPaymentMethod);
        if (detachedPaymentMethod == null) {
            throw new ServletException("Unexpected error while detaching payment method from customer");
        }

        JsonObject responseJson = new JsonObject();

        responseJson.add(JSON_KEY, JSON_PARSER.parse(getStripeKey(site)));
        responseJson.add(JSON_CARD_NUMBER, JSON_PARSER.parse(detachedPaymentMethod.getCard().getLast4()));

        return responseJson;
    }

    protected JsonObject retrievePaymentMethods(HttpServletRequest request, Site site) throws Exception {
        StripeClient client = StripeClient.getInstance(getStripeSecret(site));
        if (client == null) {
            throw new ServletException("Stripe is not configured");
        }

        Customer customer = findCustomer(client, request);
        if (customer == null) {
            throw new ServletException("Unexpected error while retrieving customer data");
        }

        JsonArray cardsJson = new JsonArray();
        for (PaymentMethod paymentMethod : client.getPaymentMethods(customer)) {
            if (paymentMethod.getCard() == null) {
                continue;
            }

            JsonObject cardJson = new JsonObject();
            cardJson.add(JSON_ID, JSON_PARSER.parse(paymentMethod.getId()));
            cardJson.add(JSON_TYPE, JSON_PARSER.parse(paymentMethod.getCard().getBrand()));
            cardJson.add(JSON_NUMBER, JSON_PARSER.parse(paymentMethod.getCard().getLast4()));
            cardsJson.add(cardJson);
        }

        JsonObject responseJson = new JsonObject();

        responseJson.add(JSON_KEY, JSON_PARSER.parse(getStripeKey(site)));
        responseJson.add(JSON_CARDS, cardsJson);

        return responseJson;
    }

    protected JsonObject createPaymentIntent(HttpServletRequest request, Site site) throws Exception {
        StripeClient client = StripeClient.getInstance(getStripeSecret(site));
        if (client == null) {
            throw new ServletException("Stripe is not configured");
        }

        JsonObject data = getPostData(request);

        List<Object> paymentMethodTypes = Collections.singletonList("card");

        Map<String, Object> params = new HashMap<>();
        params.put(StripeClient.PARAM_AMOUNT, data.get(KEY_INTENT_AMOUNT));
        params.put(StripeClient.PARAM_RECIPIENT_EMAIL, data.get(KEY_CONTACT_EMAIL));
        params.put(StripeClient.PARAM_CURRENCY, "USD");
        params.put(StripeClient.PARAM_PAYMENT_TYPE, paymentMethodTypes);

        PaymentIntent paymentIntent = client.createPaymentIntent(params);
        if (paymentIntent == null) {
            throw new ServletException("Unexpected error while creating new payment intent");
        }

        JsonObject responseJson = new JsonObject();

        responseJson.add(JSON_KEY, JSON_PARSER.parse(getStripeKey(site)));
        responseJson.add(JSON_SECRET, JSON_PARSER.parse(paymentIntent.getClientSecret()));

        return responseJson;
    }

    protected JsonObject updatePaymentMethod(HttpServletRequest request, Site site) throws Exception {
        StripeClient client = StripeClient.getInstance(getStripeSecret(site));
        if (client == null) {
            throw new ServletException("Stripe is not configured");
        }

        PaymentMethod attachedPaymentMethod = client.getPaymentMethod(request.getParameter(PARAM_CARD_ID));
        if (attachedPaymentMethod == null) {
            throw new ServletException("Unexpected error while retrieving payment method data");
        }

        Customer customer = findCustomer(client, request);
        if (customer == null) {
            throw new ServletException("Unexpected error while retrieving customer data");
        }

        Map<String, Object> invoiceSettings = new HashMap<>();
        invoiceSettings.put(StripeClient.PARAM_PAYMENT_DEFAULT, attachedPaymentMethod.getId());

        Customer updatedCustomer = client.updateCustomer(customer, invoiceSettings);
        if (updatedCustomer == null) {
            throw new ServletException("Unexpected error while updating customer");
        }

        JsonObject responseJson = new JsonObject();

        responseJson.add(JSON_KEY, JSON_PARSER.parse(getStripeKey(site)));
        responseJson.add(JSON_CARD_NUMBER, JSON_PARSER.parse(attachedPaymentMethod.getCard().getLast4()));

        return responseJson;
    }

    protected JsonObject updatePaymentIntent(HttpServletRequest request, Site site) throws Exception {
        StripeClient client = StripeClient.getInstance(getStripeSecret(site));
        if (client == null) {
            throw new ServletException("Stripe is not configured");
        }

        JsonObject data = getPostData(request);

        PaymentIntent paymentIntent = client.getPaymentIntent(request.getParameter(PARAM_INTENT_ID));
        if (paymentIntent == null) {
            throw new ServletException("Unexpected error while retrieving payment intent data");
        }

        Map<String, Object> params = new HashMap<>();
        params.put(StripeClient.PARAM_AMOUNT, data.get(KEY_INTENT_AMOUNT));

        PaymentIntent updatedPaymentIntent = client.updatePaymentIntent(paymentIntent, params);
        if (updatedPaymentIntent == null) {
            throw new ServletException("Unexpected error while updating payment intent");
        }

        JsonObject responseJson = new JsonObject();

        responseJson.add(JSON_KEY, JSON_PARSER.parse(getStripeKey(site)));
        responseJson.add(JSON_SECRET, JSON_PARSER.parse(updatedPaymentIntent.getClientSecret()));

        return responseJson;
    }

    private JsonObject getPostData(HttpServletRequest request) throws IOException {
        if (request.getContentType() != null && request.getContentType().contains("json")) {
            try (InputStream input = request.getInputStream()) {
                String jsonString = IoUtils.toString(input, StandardCharsets.UTF_8);
                return JSON_PARSER.parse(jsonString).getAsJsonObject();
            }
        }
        return new JsonObject();
    }

    private Customer findCustomer(StripeClient client, HttpServletRequest request) {
        return Optional.ofNullable(request)
            .map(r -> r.getParameter(PARAM_CONTACT_ID))
            .map(id -> DatabaseUtils.findById(StripeContact.class, id))
            .map(StripeContact::asStripeContactData)
            .map(StripeContact.Data::getContactId)
            .map(client::getCustomer)
            .orElse(null);
    }

    private boolean validatePaymentMethod(StripeClient client, Customer customer, Map<String, Object> params) throws Exception {
        try {
            return client.getPaymentMethods(customer).stream()
                .map(PaymentMethod::getCard)
                .filter(card -> card.getLast4().equals(params.get(StripeClient.PARAM_NUMBER).toString().substring(12)))
                .filter(card -> card.getExpMonth().equals(Long.valueOf(params.get(StripeClient.PARAM_EXPIRY_MONTH).toString())))
                .anyMatch(card -> card.getExpYear().equals(Long.valueOf(params.get(StripeClient.PARAM_EXPIRY_YEAR).toString())));
        } catch (Exception e) {
            throw new ServletException("Unexpected error while creating new payment method");
        }
    }

    public static String getStripeKey(Site site) {
        Function<IntegrationSiteSettings, StripeSettings> getter = IntegrationSiteSettings::getStripeSettings;
        return Optional.ofNullable(IntegrationSiteSettings.get(site, getter))
            .map(StripeSettings::getApiKey)
            .orElse(StringUtils.EMPTY);
    }

    public static String getStripeSecret(Site site) {
        Function<IntegrationSiteSettings, StripeSettings> getter = IntegrationSiteSettings::getStripeSettings;
        return Optional.ofNullable(IntegrationSiteSettings.get(site, getter))
            .map(StripeSettings::getApiSecret)
            .orElse(StringUtils.EMPTY);
    }
}
