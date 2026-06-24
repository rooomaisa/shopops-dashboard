package com.shopops.dto;

import java.time.Instant;

public record SyncStatusResponse(
        boolean shopifyConfigured,
        Instant productsLastSyncedAt,
        Instant ordersLastSyncedAt,
        long shopifySyncedProductCount,
        long shopifySyncedOrderCount,
        long demoOrderCount
) {
}
