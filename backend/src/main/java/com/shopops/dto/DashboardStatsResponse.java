package com.shopops.dto;

public record DashboardStatsResponse(
        long totalProducts,
        long openOrders,
        long openAlerts,
        long processingOrders
) {
}
