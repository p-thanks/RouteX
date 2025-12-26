package com.delivery.RouteX.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private String vehiclePlate;

    @Column(nullable = false)
    private String licenseNumber;

    private LocalDateTime licenseExpiry;

    @Column(precision = 10)
    private Double currentLatitude;

    @Column(precision = 11)
    private Double currentLongitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.OFFLINE;

    @Column(nullable = false)
    private Double rating = 5.0;

    @Column(nullable = false)
    private Integer totalDeliveries = 0;

    @Column(nullable = false)
    private Integer completedDeliveries = 0;

    @Column(nullable = false)
    private Integer cancelledDeliveries = 0;

    @Column(nullable = false)
    private Double totalEarnings = 0.0;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime lastLocationUpdate;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AvailabilityStatus {
        ONLINE,
        OFFLINE,
        BUSY
    }

    public void updateLocation(Double latitude, Double longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.lastLocationUpdate = LocalDateTime.now();
    }

    public void updateRating(Double newRating) {
        if (totalDeliveries == 0) {
            this.rating = newRating;
        } else {
            this.rating = ((this.rating * totalDeliveries) + newRating) / (totalDeliveries + 1);
        }
    }

    public void incrementDeliveries() {
        this.totalDeliveries++;
        this.completedDeliveries++;
    }

    public void incrementCancellations() {
        this.cancelledDeliveries++;
    }

    public void addEarnings(Double amount) {
        this.totalEarnings += amount;
    }

    public boolean isAvailable() {
        return availabilityStatus == AvailabilityStatus.ONLINE;
    }
}