package com.shopops.repository;

import com.shopops.entity.InternalOrderStatus;
import com.shopops.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByShopifyId(String shopifyId);
    List<Order> findByInternalStatus(InternalOrderStatus internalStatus);
    long countByInternalStatus(InternalOrderStatus internalStatus);
}
