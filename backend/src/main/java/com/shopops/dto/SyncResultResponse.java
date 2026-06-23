package com.shopops.dto;

import java.time.Instant;

public record SyncResultResponse(
        String resource,
        int created,
        int updated,
        int total,
        Instant syncedAt
) {
}
