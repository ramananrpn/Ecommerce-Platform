package com.tutorial.ecommerce.repository;

import com.tutorial.ecommerce.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByUserId(String userId, Pageable pageable);
}
