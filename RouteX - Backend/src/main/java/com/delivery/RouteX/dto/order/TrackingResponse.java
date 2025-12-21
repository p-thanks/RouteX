package com.delivery.RouteX.dto.order;

import com.delivery.RouteX.model.Order;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Builder
public class TrackingResponse {
    private Long id;
    private Order.OrderStatus status;
    private Double latitude;
    private Double longitude;
    private String notes;
    private LocalDateTime timestamp;
}
