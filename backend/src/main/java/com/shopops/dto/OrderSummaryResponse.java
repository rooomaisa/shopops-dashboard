package com.shopops.dto;

import com.shopops.entity.InternalOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryResponse(
        Long id,
        String orderNumber,
        String shopifyStatus,
        InternalOrderStatus internalStatus,
        BigDecimal totalPrice,
        String customerEmail,
        Instant orderCreatedAt,
        Instant lastSyncedAt
) {
}
