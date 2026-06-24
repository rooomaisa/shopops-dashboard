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
    private final ShopifyTokenProvider tokenProvider;
    private final RestClient restClient;

    public ShopifyClient(ShopifyProperties properties, ShopifyTokenProvider tokenProvider) {
        this.properties = properties;
        this.tokenProvider = tokenProvider;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
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
                    .header("X-Shopify-Access-Token", tokenProvider.getAccessToken())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException ex) {
            throw new ApiException(ex.getStatusCode().value(),
                    "Shopify API error: " + ex.getResponseBodyAsString());
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApiException(502, "Could not reach Shopify: " + ex.getMessage());
        }
    }

    private void ensureConfigured() {
        if (!isConfigured()) {
            throw new ApiException(503,
                    "Shopify is not configured. Add SHOPIFY_STORE_DOMAIN plus SHOPIFY_CLIENT_ID and SHOPIFY_CLIENT_SECRET to .env");
        }
    }
}
