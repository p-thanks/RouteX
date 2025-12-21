package com.delivery.RouteX.dto.order;

import com.delivery.RouteX.model.Order;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Order.OrderStatus status;

    private Long customerId;
    private String customerName;
    private String customerPhone;

    private Long driverId;
    private String driverName;
    private String driverPhone;
    private Double driverRating;

    private String pickupAddress;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private String pickupContactName;
    private String pickupContactPhone;

    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String recipientName;
    private String recipientPhone;

    private Order.PackageType packageType;
    private Double packageWeight;
    private String packageDimensions;
    private String specialInstructions;

    private LocalDateTime scheduledPickupTime;
    private LocalDateTime actualPickupTime;
    private LocalDateTime scheduledDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime createdAt;

    private Double distanceKm;
    private Double baseFare;
    private Double distanceCharge;
    private Double weightCharge;
    private Double peakHourSurcharge;
    private Double discount;
    private Double totalAmount;
    private String promoCode;

    private String deliverySignatureUrl;
    private String deliveryPhotoUrl;
    private String deliveryNotes;

    private Double customerRating;
    private String customerFeedback;

    private List<TrackingResponse> trackingHistory;
}