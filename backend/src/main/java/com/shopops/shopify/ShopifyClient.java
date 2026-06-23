package com.shopops.shopify;

import com.fasterxml.jackson.databind.JsonNode;
import com.shopops.config.ShopifyProperties;
import com.shopops.exception.ApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ShopifyClient {

    private final ShopifyProperties properties;
    private final RestClient restClient;

    public ShopifyClient(ShopifyProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("X-Shopify-Access-Token", properties.accessToken() != null ? properties.accessToken() : "")
                .build();
    }

    public boolean isConfigured() {
        return properties.isConfigured();
    }

    public JsonNode fetchProducts() {
        return get("/products.json?limit=250");
    }

    public JsonNode fetchOrders() {
        return get("/orders.json?status=any&limit=250");
    }

    private JsonNode get(String path) {
        ensureConfigured();
        try {
            return restClient.get()
                    .uri(path)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException ex) {
            throw new ApiException(ex.getStatusCode().value(),
                    "Shopify API error: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException(502, "Could not reach Shopify: " + ex.getMessage());
        }
    }

    private void ensureConfigured() {
        if (!isConfigured()) {
            throw new ApiException(503, "Shopify is not configured. Add SHOPIFY_STORE_DOMAIN and SHOPIFY_ACCESS_TOKEN to .env");
        }
    }
}
