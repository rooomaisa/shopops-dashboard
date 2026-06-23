package com.shopops.controller;

import com.shopops.dto.DashboardStatsResponse;
import com.shopops.dto.OrderDetailResponse;
import com.shopops.dto.OrderSummaryResponse;
import com.shopops.dto.ProductResponse;
import com.shopops.dto.StockAlertResponse;
import com.shopops.dto.UpdateOrderStatusRequest;
import com.shopops.service.ShopOpsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShopOpsController {

    private final ShopOpsService shopOpsService;

    public ShopOpsController(ShopOpsService shopOpsService) {
        this.shopOpsService = shopOpsService;
    }

    @GetMapping("/dashboard/stats")
    public DashboardStatsResponse stats() {
        return shopOpsService.getDashboardStats();
    }

    @GetMapping("/products")
    public List<ProductResponse> products() {
        return shopOpsService.getProducts();
    }

    @GetMapping("/orders")
    public List<OrderSummaryResponse> orders() {
        return shopOpsService.getOrders();
    }

    @GetMapping("/orders/{id}")
    public OrderDetailResponse order(@PathVariable Long id) {
        return shopOpsService.getOrder(id);
    }

    @PatchMapping("/orders/{id}/status")
    public OrderDetailResponse updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return shopOpsService.updateOrderStatus(id, request.internalStatus());
    }

    @GetMapping("/alerts")
    public List<StockAlertResponse> alerts() {
        return shopOpsService.getAlerts();
    }

    @PatchMapping("/alerts/{id}/acknowledge")
    public StockAlertResponse acknowledgeAlert(@PathVariable Long id) {
        return shopOpsService.acknowledgeAlert(id);
    }

    @PatchMapping("/alerts/{id}/resolve")
    public StockAlertResponse resolveAlert(@PathVariable Long id) {
        return shopOpsService.resolveAlert(id);
    }
}
