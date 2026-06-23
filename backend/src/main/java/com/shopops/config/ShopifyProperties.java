package com.shopops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.shopify")
public record ShopifyProperties(
        String storeDomain,
        String accessToken,
        String apiVersion
) {
    public boolean isConfigured() {
        return storeDomain != null && !storeDomain.isBlank()
                && accessToken != null && !accessToken.isBlank();
    }

    public String baseUrl() {
        String domain = (storeDomain != null ? storeDomain : "").replace("https://", "").replace("http://", "");
        if (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        return "https://" + domain + "/admin/api/" + (apiVersion != null ? apiVersion : "2025-01");
    }
}
