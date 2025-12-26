package com.delivery.RouteX.websocket;

import com.delivery.RouteX.Repository.OrderRepository;
import com.delivery.RouteX.dto.driver.UpdateLocationRequest;
import com.delivery.RouteX.model.Order;
import com.delivery.RouteX.model.User;
import com.delivery.RouteX.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * WebSocket Controller for handling incoming messages from clients
 * Processes real-time updates from drivers
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final DriverService driverService;
    private final WebSocketService webSocketService;
    private final OrderRepository orderRepository;

    /**
     * Handle driver location updates
     * Client sends to: /app/driver/location
     *
     * @param request Location update request with lat/lng
     * @param user Authenticated driver user
     */
    @MessageMapping("/driver/location")
    public void handleDriverLocationUpdate(
            @Payload UpdateLocationRequest request,
            @AuthenticationPrincipal User user
    ) {
        // Validate that sender is a driver
        if (user == null || user.getRole() != User.Role.DRIVER) {
            log.warn("Non-driver attempted to send location update");
            return;
        }

        // Update driver location in database
        driverService.updateDriverLocation(user.getId(), request);

        // Get all active orders for this driver
        Long driverId = user.getDriver().getId();
        List<Order> activeOrders = orderRepository.findActiveOrdersByDriver(driverId);

        // Broadcast location to all active orders
        for (Order order : activeOrders) {
            webSocketService.sendLocationUpdate(
                    order.getId(),
                    request.getLatitude(),
                    request.getLongitude()
            );
        }

        log.debug("Driver {} location updated via WebSocket: {}, {}",
                driverId, request.getLatitude(), request.getLongitude());
    }

    /**
     * Handle connection events
     * Called when client connects to WebSocket
     */
    @MessageMapping("/connect")
    public void handleConnect(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("WebSocket client connected: session {}", sessionId);
    }

    /**
     * Handle disconnect events
     * Called when client disconnects from WebSocket
     */
    @MessageMapping("/disconnect")
    public void handleDisconnect(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("WebSocket client disconnected: session {}", sessionId);
    }
}