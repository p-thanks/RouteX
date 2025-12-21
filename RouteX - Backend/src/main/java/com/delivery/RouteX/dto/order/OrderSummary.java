package com.delivery.RouteX.dto.order;

import com.delivery.RouteX.model.Order;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderSummary {
    private Long id;
    private String orderNumber;
    private Order.OrderStatus status;
    private String pickupAddress;
    private String deliveryAddress;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private String driverName;
}
