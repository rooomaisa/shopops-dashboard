package com.shopops.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.shopops.dto.SyncResultResponse;
import com.shopops.dto.SyncStatusResponse;
import com.shopops.entity.InternalOrderStatus;
import com.shopops.entity.Order;
import com.shopops.entity.OrderLine;
import com.shopops.entity.Product;
import com.shopops.repository.OrderRepository;
import com.shopops.repository.ProductRepository;
import com.shopops.shopify.ShopifyClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ShopifySyncService {

    private final ShopifyClient shopifyClient;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ShopifySyncService(
            ShopifyClient shopifyClient,
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        this.shopifyClient = shopifyClient;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public SyncStatusResponse getSyncStatus() {
        var products = productRepository.findAll();
        var orders = orderRepository.findAll();

        return new SyncStatusResponse(
                shopifyClient.isConfigured(),
                products.stream()
                        .map(Product::getLastSyncedAt)
                        .filter(java.util.Objects::nonNull)
                        .max(Comparator.naturalOrder())
                        .orElse(null),
                orders.stream()
                        .map(Order::getLastSyncedAt)
                        .filter(java.util.Objects::nonNull)
                        .max(Comparator.naturalOrder())
                        .orElse(null),
                products.stream().filter(p -> p.getLastSyncedAt() != null).count(),
                orders.stream().filter(o -> o.getLastSyncedAt() != null).count(),
                orders.stream().filter(o -> o.getLastSyncedAt() == null).count()
        );
    }

    @Transactional
    public SyncResultResponse syncProducts() {
        JsonNode root = shopifyClient.fetchProducts();
        JsonNode products = root.path("products");
        Instant syncedAt = Instant.now();
        int created = 0;
        int updated = 0;

        for (JsonNode node : products) {
            String shopifyId = toShopifyId("Product", node.path("id").asText());
            Optional<Product> existing = productRepository.findByShopifyId(shopifyId);
            Product product = existing.orElseGet(Product::new);
            boolean isNew = product.getId() == null;

            product.setShopifyId(shopifyId);
            product.setTitle(node.path("title").asText("Untitled"));
            product.setImageUrl(extractImageUrl(node));

            JsonNode variant = node.path("variants").isArray() && !node.path("variants").isEmpty()
                    ? node.path("variants").get(0)
                    : null;
            if (variant != null) {
                product.setSku(textOrNull(variant, "sku"));
                product.setPrice(parseDecimal(variant.path("price").asText("0")));
                product.setInventoryQuantity(variant.path("inventory_quantity").asInt(0));
            } else {
                product.setPrice(BigDecimal.ZERO);
                product.setInventoryQuantity(0);
            }

            product.setLastSyncedAt(syncedAt);
            productRepository.save(product);

            if (isNew) created++;
            else updated++;
        }

        return new SyncResultResponse("products", created, updated, created + updated, syncedAt);
    }

    @Transactional
    public SyncResultResponse syncOrders() {
        JsonNode root = shopifyClient.fetchOrders();
        JsonNode orders = root.path("orders");
        Instant syncedAt = Instant.now();
        int created = 0;
        int updated = 0;

        Map<String, Product> productCache = new HashMap<>();

        for (JsonNode node : orders) {
            String shopifyId = toShopifyId("Order", node.path("id").asText());
            Optional<Order> existing = orderRepository.findByShopifyId(shopifyId);
            Order order = existing.orElseGet(Order::new);
            boolean isNew = order.getId() == null;

            order.setShopifyId(shopifyId);
            order.setOrderNumber(node.path("name").asText("#?"));
            order.setShopifyStatus(node.path("financial_status").asText("unknown"));
            if (isNew) {
                order.setInternalStatus(InternalOrderStatus.NEW);
            }
            order.setTotalPrice(parseDecimal(node.path("total_price").asText("0")));
            order.setCustomerEmail(null);
            order.setOrderCreatedAt(parseInstant(node.path("created_at").asText()));
            order.setLastSyncedAt(syncedAt);

            order.getLines().clear();
            for (JsonNode lineNode : node.path("line_items")) {
                OrderLine line = new OrderLine();
                line.setOrder(order);
                line.setTitle(lineNode.path("title").asText("Item"));
                line.setQuantity(lineNode.path("quantity").asInt(1));
                line.setUnitPrice(parseDecimal(lineNode.path("price").asText("0")));

                String productShopifyId = toShopifyId("Product", lineNode.path("product_id").asText());
                if (!productShopifyId.isBlank() && !"gid://shopify/Product/".equals(productShopifyId)) {
                    Product product = productCache.computeIfAbsent(productShopifyId, id ->
                            productRepository.findByShopifyId(id).orElse(null));
                    line.setProduct(product);
                }

                order.getLines().add(line);
            }

            orderRepository.save(order);
            if (isNew) created++;
            else updated++;
        }

        return new SyncResultResponse("orders", created, updated, created + updated, syncedAt);
    }

    private String toShopifyId(String type, String rawId) {
        if (rawId == null || rawId.isBlank() || "null".equals(rawId)) {
            return "";
        }
        if (rawId.startsWith("gid://")) {
            return rawId;
        }
        return "gid://shopify/" + type + "/" + rawId;
    }

    private String extractImageUrl(JsonNode product) {
        if (!product.path("image").isMissingNode() && product.path("image").has("src")) {
            return product.path("image").path("src").asText();
        }
        if (product.path("images").isArray() && !product.path("images").isEmpty()) {
            return product.path("images").get(0).path("src").asText(null);
        }
        return null;
    }

    private String textOrNull(JsonNode node, String field) {
        String value = node.path(field).asText(null);
        return value == null || value.isBlank() ? null : value;
    }

    private BigDecimal parseDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private Instant parseInstant(String value) {
        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (Exception ex) {
            return Instant.now();
        }
    }
}
