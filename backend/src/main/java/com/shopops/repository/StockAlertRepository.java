package com.shopops.repository;

import com.shopops.entity.AlertStatus;
import com.shopops.entity.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    List<StockAlert> findByStatus(AlertStatus status);
    long countByStatus(AlertStatus status);
    boolean existsByProductIdAndStatus(Long productId, AlertStatus status);
    List<StockAlert> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}
