package com.shopops.controller;

import com.shopops.dto.SyncResultResponse;
import com.shopops.dto.SyncStatusResponse;
import com.shopops.service.ShopifySyncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final ShopifySyncService shopifySyncService;

    public SyncController(ShopifySyncService shopifySyncService) {
        this.shopifySyncService = shopifySyncService;
    }

    @GetMapping("/status")
    public SyncStatusResponse status() {
        return shopifySyncService.getSyncStatus();
    }

    @PostMapping("/products")
    public SyncResultResponse syncProducts() {
        return shopifySyncService.syncProducts();
    }

    @PostMapping("/orders")
    public SyncResultResponse syncOrders() {
        return shopifySyncService.syncOrders();
    }

    @PostMapping("/all")
    public List<SyncResultResponse> syncAll() {
        SyncResultResponse products = shopifySyncService.syncProducts();
        SyncResultResponse orders = shopifySyncService.syncOrders();
        return List.of(products, orders);
    }
}
