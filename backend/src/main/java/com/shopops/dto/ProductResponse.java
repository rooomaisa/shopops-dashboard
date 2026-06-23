package com.shopops.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String shopifyId,
        String title,
        String sku,
        BigDecimal price,
        Integer inventoryQuantity,
        String imageUrl,
        Instant lastSyncedAt
) {
}
