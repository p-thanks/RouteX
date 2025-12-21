package com.delivery.RouteX.Repository;

import com.delivery.RouteX.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByDriverId(Long driverId);

    List<Order> findByStatus(Order.OrderStatus status);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    Page<Order> findByDriverId(Long driverId, Pageable pageable);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status " +
            "AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByStatusAndDateRange(
            @Param("status") Order.OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId " +
            "AND o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findCustomerOrdersByStatuses(
            @Param("customerId") Long customerId,
            @Param("statuses") List<Order.OrderStatus> statuses
    );

    @Query("SELECT o FROM Order o WHERE o.driver.id = :driverId " +
            "AND o.status IN ('ASSIGNED', 'PICKED_UP', 'IN_TRANSIT') " +
            "ORDER BY o.scheduledPickupTime ASC")
    List<Order> findActiveOrdersByDriver(@Param("driverId") Long driverId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") Order.OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED' " +
            "AND o.actualDeliveryTime BETWEEN :startDate AND :endDate")
    Double getTotalRevenueInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Long countOrdersInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.customer.user.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.customer.user.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.pickupAddress) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.deliveryAddress) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Order> searchOrders(@Param("searchTerm") String searchTerm, Pageable pageable);
}
