package com.delivery.RouteX.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    private String relatedEntityId;
    private String actionUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public enum NotificationType {
        ORDER_CREATED,
        ORDER_ASSIGNED,
        ORDER_PICKED_UP,
        ORDER_IN_TRANSIT,
        ORDER_DELIVERED,
        ORDER_CANCELLED,
        ORDER_FAILED,
        DRIVER_ASSIGNED,
        PAYMENT_RECEIVED,
        RATING_RECEIVED,
        SYSTEM_ALERT,
        PROMOTIONAL
    }
}