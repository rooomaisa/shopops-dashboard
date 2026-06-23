package com.shopops.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long id,
        String title,
        Integer quantity,
        BigDecimal unitPrice
) {
}
