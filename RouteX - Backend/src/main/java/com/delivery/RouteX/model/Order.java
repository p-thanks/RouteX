package com.delivery.RouteX.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false, precision = 10)
    private Double pickupLatitude;

    @Column(nullable = false, precision = 11)
    private Double pickupLongitude;

    @Column(nullable = false)
    private String pickupContactName;

    @Column(nullable = false)
    private String pickupContactPhone;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false, precision = 10)
    private Double deliveryLatitude;

    @Column(nullable = false, precision = 11)
    private Double deliveryLongitude;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageType packageType;

    @Column(nullable = false)
    private Double packageWeight;

    private String packageDimensions;

    @Column(length = 1000)
    private String specialInstructions;

    private String packagePhotoUrl;

    private LocalDateTime scheduledPickupTime;
    private LocalDateTime actualPickupTime;
    private LocalDateTime scheduledDeliveryTime;
    private LocalDateTime actualDeliveryTime;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Double baseFare;

    @Column(nullable = false)
    private Double distanceCharge;

    @Column(nullable = false)
    private Double weightCharge;

    private Double peakHourSurcharge = 0.0;
    private Double discount = 0.0;

    @Column(nullable = false)
    private Double totalAmount;

    private String promoCode;

    private String deliverySignatureUrl;
    private String deliveryPhotoUrl;

    @Column(length = 500)
    private String deliveryNotes;

    private Double customerRating;

    @Column(length = 1000)
    private String customerFeedback;

    private Double driverRating;

    @Column(length = 1000)
    private String driverFeedback;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp DESC")
    private List<OrderTracking> trackingHistory = new ArrayList<>();

    private String cancellationReason;
    private LocalDateTime cancelledAt;

    @PrePersist
    protected void onCreate() {
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void assignDriver(Driver driver) {
        this.driver = driver;
        this.status = OrderStatus.ASSIGNED;
        addTracking(OrderStatus.ASSIGNED, null, "Driver assigned");
    }

    public void markPickedUp() {
        this.status = OrderStatus.PICKED_UP;
        this.actualPickupTime = LocalDateTime.now();
        addTracking(OrderStatus.PICKED_UP, null, "Package picked up");
    }

    public void markInTransit() {
        this.status = OrderStatus.IN_TRANSIT;
        addTracking(OrderStatus.IN_TRANSIT, null, "Package in transit");
    }

    public void markDelivered(String signatureUrl, String photoUrl, String notes) {
        this.status = OrderStatus.DELIVERED;
        this.actualDeliveryTime = LocalDateTime.now();
        this.deliverySignatureUrl = signatureUrl;
        this.deliveryPhotoUrl = photoUrl;
        this.deliveryNotes = notes;
        addTracking(OrderStatus.DELIVERED, null, "Package delivered successfully");
    }

    public void cancel(String reason) {
        this.status = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = LocalDateTime.now();
        addTracking(OrderStatus.CANCELLED, null, "Order cancelled: " + reason);
    }

    public void markFailed(String reason) {
        this.status = OrderStatus.FAILED;
        addTracking(OrderStatus.FAILED, null, "Delivery failed: " + reason);
    }

    private void addTracking(OrderStatus status, String notes, String message) {
        OrderTracking tracking = OrderTracking.builder()
                .order(this)
                .status(status)
                .notes(notes != null ? notes : message)
                .timestamp(LocalDateTime.now())
                .build();

        if (driver != null) {
            tracking.setLatitude(driver.getCurrentLatitude());
            tracking.setLongitude(driver.getCurrentLongitude());
        }  

        trackingHistory.add(tracking);
    }

    public boolean canBeAssigned() {
        return status == OrderStatus.PENDING;
    }

    public boolean canBePickedUp() {
        return status == OrderStatus.ASSIGNED;
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.ASSIGNED;
    }

    public enum OrderStatus {
        PENDING,
        ASSIGNED,
        PICKED_UP,
        IN_TRANSIT,
        DELIVERED,
        CANCELLED,
        FAILED
    }

    public enum PackageType {
        DOCUMENT,
        PARCEL,
        FOOD,
        FRAGILE,
        ELECTRONICS,
        CLOTHING,
        OTHER
    }
}