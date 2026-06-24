package com.shopops.shopify;

import com.fasterxml.jackson.databind.JsonNode;
import com.shopops.exception.ApiException;
import org.springframework.web.client.RestClientResponseException;

final class ShopifyApiErrors {

    private ShopifyApiErrors() {
    }

    static ApiException fromResponse(RestClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        return new ApiException(ex.getStatusCode().value(), friendlyMessage(body));
    }

    static String friendlyMessage(String body) {
        if (body == null || body.isBlank()) {
            return "Shopify returned an empty error response";
        }

        if (body.contains("protected customer data") || body.contains("protected customer fields")) {
            return "Shopify blocked order data (protected customer info). Products can still sync. "
                    + "Orders will sync without customer email, or use seed orders for the demo.";
        }

        if (body.contains("shop_not_permitted")) {
            return "Shopify rejected this store for your app. Create the dev store from dev.shopify.com → Winkels.";
        }

        if (body.contains("Store unavailable") || body.contains("This store will be right back")) {
            return "Shopify store not found or offline. Check SHOPIFY_STORE_DOMAIN in backend/.env.";
        }

        if (body.trim().startsWith("{")) {
            try {
                JsonNode json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
                if (json.has("error_description")) {
                    return "Shopify error: " + json.path("error_description").asText();
                }
                if (json.has("errors")) {
                    return "Shopify error: " + json.path("errors").asText();
                }
            } catch (Exception ignored) {
                // fall through
            }
        }

        if (body.trim().startsWith("<!DOCTYPE") || body.trim().startsWith("<html")) {
            return "Shopify returned an HTML error page. Check SHOPIFY_STORE_DOMAIN in backend/.env.";
        }

        String trimmed = body.length() > 300 ? body.substring(0, 300) + "…" : body;
        return "Shopify error: " + trimmed;
    }
}
