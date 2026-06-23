package com.shopops.repository;

import com.shopops.entity.InternalOrderStatus;
import com.shopops.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByShopifyId(String shopifyId);
    List<Order> findByInternalStatus(InternalOrderStatus internalStatus);
    long countByInternalStatus(InternalOrderStatus internalStatus);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.lines WHERE o.id = :id")
    Optional<Order> findByIdWithLines(Long id);
}
