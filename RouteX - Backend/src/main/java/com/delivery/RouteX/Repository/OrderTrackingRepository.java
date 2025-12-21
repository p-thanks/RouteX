package com.delivery.RouteX.Repository;

import com.delivery.RouteX.model.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {

    List<OrderTracking> findByOrderIdOrderByTimestampDesc(Long orderId);

    List<OrderTracking> findByOrderOrderNumberOrderByTimestampDesc(String orderNumber);

    @Query("SELECT ot FROM OrderTracking ot WHERE ot.order.id = :orderId " +
            "ORDER BY ot.timestamp DESC")
    List<OrderTracking> getOrderTrackingHistory(@Param("orderId") Long orderId);
}