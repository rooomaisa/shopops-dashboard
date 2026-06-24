package com.shopops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.shopify")
public record ShopifyProperties(
        String storeDomain,
        String accessToken,
        String clientId,
        String clientSecret,
        String apiVersion
) {
    public boolean isConfigured() {
        return storeDomain != null && !storeDomain.isBlank()
                && (hasClientCredentials() || hasAccessToken());
    }

    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.isBlank();
    }

    public boolean hasClientCredentials() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    public String shopDomain() {
        String domain = (storeDomain != null ? storeDomain : "")
                .replace("https://", "")
                .replace("http://", "");
        if (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        return domain;
    }

    public String baseUrl() {
        return "https://" + shopDomain() + "/admin/api/" + (apiVersion != null ? apiVersion : "2025-01");
    }

    public String tokenUrl() {
        return "https://" + shopDomain() + "/admin/oauth/access_token";
    }
}
