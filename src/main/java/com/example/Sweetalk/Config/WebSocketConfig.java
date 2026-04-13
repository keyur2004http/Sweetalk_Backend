package com.example.Sweetalk.Config;

import com.example.Sweetalk.DTO.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Arrays;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private SimpMessageSendingOperations messagingTemplate;

    @Value("${ALLOWED_ORIGINS}")
    private String allowedOrigins;


    public void WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(Arrays.stream(allowedOrigins.split(","))
                                .map(String::trim)
                        .toArray(String[]::new))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
        @EventListener
        public void handleWebSocketConnectListener(SessionConnectedEvent event) {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String username = (String) headerAccessor.getSessionAttributes().get("username");
            if (username != null) {

                messagingTemplate.convertAndSend("/topic/online-users", new UserStatus(username, true));
            }
        }

        @EventListener
        public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String username = (String) headerAccessor.getSessionAttributes().get("username");
            if (username != null) {
                // Broadcast that user is offline
                messagingTemplate.convertAndSend("/topic/online-users", new UserStatus(username, false));
            }
        }
    }

