package com.delivery.RouteX.websocket;

import com.delivery.RouteX.Repository.OrderRepository;
import com.delivery.RouteX.dto.websocket.LocationUpdateMessage;
import com.delivery.RouteX.dto.websocket.OrderUpdateMessage;
import com.delivery.RouteX.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * WebSocket Service for real-time message broadcasting
 * Handles sending live updates to connected clients
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {


    private final SimpMessagingTemplate messagingTemplate;
    private final OrderRepository orderRepository;

    /**
     * Send order status update to subscribed clients
     * @param orderId The order ID to send update for
     */
    public void sendOrderUpdate(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            log.warn("Cannot send order update: Order {} not found", orderId);
            return;
        }

        OrderUpdateMessage message = OrderUpdateMessage.builder()
                .orderId(orderId)
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .message("Order status updated to " + order.getStatus())
                .timestamp(LocalDateTime.now())
                .build();

        // Send to specific order topic (public tracking)
        messagingTemplate.convertAndSend("/topic/orders/" + orderId, message);

        // Send to customer's private queue
        messagingTemplate.convertAndSend(
                "/queue/customer/" + order.getCustomer().getId(),
                message
        );

        // Send to driver's private queue if assigned
        if (order.getDriver() != null) {
            messagingTemplate.convertAndSend(
                    "/queue/driver/" + order.getDriver().getId(),
                    message
            );
        }

        log.debug("Order update sent via WebSocket: {}", orderId);
    }

    /**
     * Send driver location update for real-time tracking
     * @param orderId The order being tracked
     * @param latitude Driver's current latitude
     * @param longitude Driver's current longitude
     */
    public void sendLocationUpdate(Long orderId, Double latitude, Double longitude) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getDriver() == null) {
            log.warn("Cannot send location update: Order {} not found or no driver assigned", orderId);
            return;
        }

        LocationUpdateMessage message = LocationUpdateMessage.builder()
                .orderId(orderId)
                .driverId(order.getDriver().getId())
                .latitude(latitude)
                .longitude(longitude)
                .timestamp(LocalDateTime.now())
                .build();

        // Send to order tracking topic (public + customer)
        messagingTemplate.convertAndSend("/topic/tracking/" + orderId, message);

        log.debug("Location update sent via WebSocket for order: {}", orderId);
    }

    /**
     * Notify all admins and dispatchers
     * @param message The message to broadcast
     */
    public void notifyAdmins(String message) {
        messagingTemplate.convertAndSend("/topic/admin", message);
        log.debug("Admin notification sent: {}", message);
    }

    /**
     * Send custom message to specific user
     * @param userId The user ID to send to
     * @param message The message to send
     */
    public void sendMessageToUser(Long userId, String message) {
        messagingTemplate.convertAndSend("/queue/user/" + userId, message);
        log.debug("Message sent to user {}: {}", userId, message);
    }
}
