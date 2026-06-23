package com.shopops.config;

import com.shopops.entity.AlertStatus;
import com.shopops.entity.InternalOrderStatus;
import com.shopops.entity.Order;
import com.shopops.entity.OrderLine;
import com.shopops.entity.Product;
import com.shopops.entity.StockAlert;
import com.shopops.repository.OrderRepository;
import com.shopops.entity.User;
import com.shopops.repository.ProductRepository;
import com.shopops.repository.StockAlertRepository;
import com.shopops.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class SeedDataRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedDataRunner.class);

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockAlertRepository stockAlertRepository;
    private final UserRepository userRepository;

    public SeedDataRunner(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            StockAlertRepository stockAlertRepository,
            UserRepository userRepository
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.stockAlertRepository = stockAlertRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Seed data already present — skipping");
            return;
        }

        log.info("Seeding demo products, orders, and alerts...");

        Product candle = saveProduct("gid://shopify/Product/1", "Lavender Candle", "LC-001",
                "14.99", 3, "https://placehold.co/200x200?text=Candle");
        Product mug = saveProduct("gid://shopify/Product/2", "Ceramic Mug", "CM-002",
                "12.00", 2, "https://placehold.co/200x200?text=Mug");
        Product soap = saveProduct("gid://shopify/Product/3", "Handmade Soap", "HS-003",
                "8.50", 50, "https://placehold.co/200x200?text=Soap");
        saveProduct("gid://shopify/Product/4", "Gift Box Set", "GB-004",
                "29.99", 8, "https://placehold.co/200x200?text=Gift");

        User owner = new User();
        owner.setName("Bloom & Co Owner");
        owner.setEmail("owner@bloomandco.example");
        owner.setPassword("placeholder-until-auth-phase");
        userRepository.save(owner);

        Order order1 = new Order();
        order1.setShopifyId("gid://shopify/Order/1001");
        order1.setOrderNumber("#1001");
        order1.setShopifyStatus("paid");
        order1.setInternalStatus(InternalOrderStatus.NEW);
        order1.setTotalPrice(new BigDecimal("26.99"));
        order1.setCustomerEmail("customer1@example.com");
        order1.setOrderCreatedAt(Instant.now().minus(2, ChronoUnit.DAYS));
        order1.setLastSyncedAt(Instant.now());
        order1.getLines().add(line(order1, candle, 1, "14.99"));
        order1.getLines().add(line(order1, mug, 1, "12.00"));
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setShopifyId("gid://shopify/Order/1002");
        order2.setOrderNumber("#1002");
        order2.setShopifyStatus("paid");
        order2.setInternalStatus(InternalOrderStatus.PROCESSING);
        order2.setTotalPrice(new BigDecimal("8.50"));
        order2.setCustomerEmail("customer2@example.com");
        order2.setOrderCreatedAt(Instant.now().minus(1, ChronoUnit.DAYS));
        order2.setLastSyncedAt(Instant.now());
        order2.getLines().add(line(order2, soap, 1, "8.50"));
        orderRepository.save(order2);

        StockAlert candleAlert = new StockAlert();
        candleAlert.setProduct(candle);
        candleAlert.setStatus(AlertStatus.OPEN);
        candleAlert.setThreshold(5);
        candleAlert.setQuantityAtAlert(3);
        stockAlertRepository.save(candleAlert);

        StockAlert mugAlert = new StockAlert();
        mugAlert.setProduct(mug);
        mugAlert.setStatus(AlertStatus.ACKNOWLEDGED);
        mugAlert.setThreshold(5);
        mugAlert.setQuantityAtAlert(2);
        stockAlertRepository.save(mugAlert);

        log.info("Seed complete: {} products, {} orders, {} alerts",
                productRepository.count(), orderRepository.count(), stockAlertRepository.count());
    }

    private Product saveProduct(String shopifyId, String title, String sku,
                                String price, int stock, String imageUrl) {
        Product product = new Product();
        product.setShopifyId(shopifyId);
        product.setTitle(title);
        product.setSku(sku);
        product.setPrice(new BigDecimal(price));
        product.setInventoryQuantity(stock);
        product.setImageUrl(imageUrl);
        product.setLastSyncedAt(Instant.now());
        return productRepository.save(product);
    }

    private OrderLine line(Order order, Product product, int qty, String unitPrice) {
        OrderLine line = new OrderLine();
        line.setOrder(order);
        line.setProduct(product);
        line.setTitle(product.getTitle());
        line.setQuantity(qty);
        line.setUnitPrice(new BigDecimal(unitPrice));
        return line;
    }
}
