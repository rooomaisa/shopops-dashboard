package com.shopops.dto;

import java.util.List;

public record SyncAllResponse(
        List<SyncResultResponse> results,
        List<String> warnings
) {
}
