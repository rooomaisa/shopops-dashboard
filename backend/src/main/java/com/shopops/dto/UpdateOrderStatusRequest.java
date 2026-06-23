package com.shopops.dto;

import com.shopops.entity.InternalOrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required")
        InternalOrderStatus internalStatus
) {
}
