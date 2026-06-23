package com.shopops.dto;

import com.shopops.entity.InternalOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDetailResponse(
        Long id,
        String shopifyId,
        String orderNumber,
        String shopifyStatus,
        InternalOrderStatus internalStatus,
        BigDecimal totalPrice,
        String customerEmail,
        Instant orderCreatedAt,
        Instant lastSyncedAt,
        List<OrderLineResponse> lines
) {
}
