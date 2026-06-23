package com.shopops.repository;

import com.shopops.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByShopifyId(String shopifyId);
    boolean existsByShopifyId(String shopifyId);
}
