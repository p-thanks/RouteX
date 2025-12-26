package com.delivery.RouteX.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration
 * Configures STOMP messaging over WebSocket for real-time communication
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker
     * - /topic: Public broadcast (many subscribers)
     * - /queue: Private messages (one subscriber)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple in-memory message broker
        config.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages from client to server
        config.setApplicationDestinationPrefixes("/app");

        // Optional: Prefix for user-specific destinations
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Register STOMP endpoints
     * Clients connect to: ws://localhost:8080/ws
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                        "http://localhost:3000",    // React dev server
                        "http://localhost:5173"     // Vite dev server
                )
                .withSockJS();  // Fallback for browsers without WebSocket support
    }
}
