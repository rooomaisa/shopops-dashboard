package com.shopops.service;

import com.shopops.dto.DashboardStatsResponse;
import com.shopops.dto.OrderDetailResponse;
import com.shopops.dto.OrderLineResponse;
import com.shopops.dto.OrderSummaryResponse;
import com.shopops.dto.ProductResponse;
import com.shopops.dto.StockAlertResponse;
import com.shopops.entity.AlertStatus;
import com.shopops.entity.InternalOrderStatus;
import com.shopops.entity.Order;
import com.shopops.entity.StockAlert;
import com.shopops.exception.ApiException;
import com.shopops.repository.OrderRepository;
import com.shopops.repository.ProductRepository;
import com.shopops.repository.StockAlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ShopOpsService {

    private static final Map<InternalOrderStatus, Set<InternalOrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            InternalOrderStatus.NEW, EnumSet.of(InternalOrderStatus.PROCESSING),
            InternalOrderStatus.PROCESSING, EnumSet.of(InternalOrderStatus.SHIPPED),
            InternalOrderStatus.SHIPPED, EnumSet.of(InternalOrderStatus.COMPLETED),
            InternalOrderStatus.COMPLETED, EnumSet.noneOf(InternalOrderStatus.class)
    );

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockAlertRepository stockAlertRepository;

    public ShopOpsService(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            StockAlertRepository stockAlertRepository
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.stockAlertRepository = stockAlertRepository;
    }

    public DashboardStatsResponse getDashboardStats() {
        long openOrders = orderRepository.countByInternalStatus(InternalOrderStatus.NEW)
                + orderRepository.countByInternalStatus(InternalOrderStatus.PROCESSING);
        return new DashboardStatsResponse(
                productRepository.count(),
                openOrders,
                stockAlertRepository.countByStatus(AlertStatus.OPEN),
                orderRepository.countByInternalStatus(InternalOrderStatus.PROCESSING)
        );
    }

    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(
                        p.getId(), p.getShopifyId(), p.getTitle(), p.getSku(),
                        p.getPrice(), p.getInventoryQuantity(), p.getImageUrl(), p.getLastSyncedAt()
                ))
                .toList();
    }

    public List<OrderSummaryResponse> getOrders() {
        return orderRepository.findAll().stream()
                .sorted((a, b) -> b.getOrderCreatedAt().compareTo(a.getOrderCreatedAt()))
                .map(this::toSummary)
                .toList();
    }

    public OrderDetailResponse getOrder(Long id) {
        Order order = orderRepository.findByIdWithLines(id)
                .orElseThrow(() -> new ApiException(404, "Order not found"));
        return toDetail(order);
    }

    @Transactional
    public OrderDetailResponse updateOrderStatus(Long id, InternalOrderStatus newStatus) {
        Order order = orderRepository.findByIdWithLines(id)
                .orElseThrow(() -> new ApiException(404, "Order not found"));

        InternalOrderStatus current = order.getInternalStatus();
        Set<InternalOrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new ApiException(400, "Cannot change status from " + current + " to " + newStatus);
        }

        order.setInternalStatus(newStatus);
        return toDetail(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<StockAlertResponse> getAlerts() {
        return stockAlertRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toAlert)
                .toList();
    }

    @Transactional
    public StockAlertResponse acknowledgeAlert(Long id) {
        return updateAlertStatus(id, AlertStatus.OPEN, AlertStatus.ACKNOWLEDGED);
    }

    @Transactional
    public StockAlertResponse resolveAlert(Long id) {
        StockAlert alert = stockAlertRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "Alert not found"));

        if (alert.getStatus() == AlertStatus.RESOLVED) {
            throw new ApiException(400, "Alert is already resolved");
        }

        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(Instant.now());
        return toAlert(stockAlertRepository.save(alert));
    }

    private StockAlertResponse updateAlertStatus(Long id, AlertStatus expected, AlertStatus next) {
        StockAlert alert = stockAlertRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "Alert not found"));

        if (alert.getStatus() != expected) {
            throw new ApiException(400, "Alert is not in " + expected + " status");
        }

        alert.setStatus(next);
        return toAlert(stockAlertRepository.save(alert));
    }

    private OrderSummaryResponse toSummary(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getShopifyStatus(),
                order.getInternalStatus(),
                order.getTotalPrice(),
                order.getCustomerEmail(),
                order.getOrderCreatedAt()
        );
    }

    private OrderDetailResponse toDetail(Order order) {
        List<OrderLineResponse> lines = order.getLines().stream()
                .map(line -> new OrderLineResponse(
                        line.getId(), line.getTitle(), line.getQuantity(), line.getUnitPrice()
                ))
                .toList();

        return new OrderDetailResponse(
                order.getId(),
                order.getShopifyId(),
                order.getOrderNumber(),
                order.getShopifyStatus(),
                order.getInternalStatus(),
                order.getTotalPrice(),
                order.getCustomerEmail(),
                order.getOrderCreatedAt(),
                order.getLastSyncedAt(),
                lines
        );
    }

    private StockAlertResponse toAlert(StockAlert alert) {
        return new StockAlertResponse(
                alert.getId(),
                alert.getProduct().getId(),
                alert.getProduct().getTitle(),
                alert.getStatus(),
                alert.getThreshold(),
                alert.getQuantityAtAlert(),
                alert.getCreatedAt(),
                alert.getResolvedAt()
        );
    }
}
