package com.shopops.dto;

import com.shopops.entity.AlertStatus;

import java.time.Instant;

public record StockAlertResponse(
        Long id,
        Long productId,
        String productTitle,
        AlertStatus status,
        Integer threshold,
        Integer quantityAtAlert,
        Instant createdAt,
        Instant resolvedAt
) {
}
