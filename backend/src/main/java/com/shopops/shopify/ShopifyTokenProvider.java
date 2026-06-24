package com.shopops.shopify;

import com.fasterxml.jackson.databind.JsonNode;
import com.shopops.config.ShopifyProperties;
import com.shopops.exception.ApiException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;

@Component
public class ShopifyTokenProvider {

    private final ShopifyProperties properties;
    private final RestClient tokenClient;

    private volatile String cachedToken;
    private volatile Instant tokenExpiresAt;

    public ShopifyTokenProvider(ShopifyProperties properties) {
        this.properties = properties;
        this.tokenClient = RestClient.create();
    }

    public String getAccessToken() {
        if (properties.hasClientCredentials()) {
            return fetchClientCredentialsToken();
        }
        if (properties.hasAccessToken()) {
            return properties.accessToken();
        }
        throw new ApiException(503,
                "Shopify is not configured. Add SHOPIFY_CLIENT_ID and SHOPIFY_CLIENT_SECRET (or SHOPIFY_ACCESS_TOKEN) to .env");
    }

    private synchronized String fetchClientCredentialsToken() {
        if (cachedToken != null && tokenExpiresAt != null
                && Instant.now().isBefore(tokenExpiresAt.minusSeconds(60))) {
            return cachedToken;
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", properties.clientId());
        body.add("client_secret", properties.clientSecret());

        try {
            JsonNode response = tokenClient.post()
                    .uri(properties.tokenUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null || !response.hasNonNull("access_token")) {
                throw new ApiException(502, "Shopify token response was missing access_token");
            }

            cachedToken = response.path("access_token").asText();
            int expiresIn = response.path("expires_in").asInt(86_400);
            tokenExpiresAt = Instant.now().plusSeconds(expiresIn);
            return cachedToken;
        } catch (RestClientResponseException ex) {
            throw new ApiException(ex.getStatusCode().value(),
                    "Shopify token error: " + ex.getResponseBodyAsString());
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApiException(502, "Could not fetch Shopify access token: " + ex.getMessage());
        }
    }
}
