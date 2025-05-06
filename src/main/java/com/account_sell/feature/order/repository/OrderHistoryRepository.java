package com.account_sell.feature.order.repository;

import com.account_sell.enumation.OrderStatus;
import com.account_sell.feature.order.models.OrderHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {
    // Find history by order ID with pagination
    Page<OrderHistoryEntity> findByOrderIdOrderByCreatedAtDesc(Long orderId, Pageable pageable);
    
    // Search history with status filter and search
    @Query("SELECT h FROM OrderHistoryEntity h WHERE " +
           "(:status IS NULL OR h.newStatus = :status) AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(h.order.accountNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(h.order.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(h.order.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY h.createdAt DESC")
    Page<OrderHistoryEntity> searchOrderHistory(
            @Param("status") OrderStatus status,
            @Param("search") String search,
            Pageable pageable);
}