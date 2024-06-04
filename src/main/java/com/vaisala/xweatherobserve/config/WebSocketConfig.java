package com.vaisala.xweatherobserve.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Registering STOMP endpoints and set allowed origins
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Register the "/ws" endpoint
              .setAllowedOrigins("http://localhost:3000", "https://barnprojectjava.azurewebsites.net") // Allowing requests from this origin
            .withSockJS(); 
    }

    // Configure the message broker
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // Prefix for messages from client to server
        registry.enableSimpleBroker("/topic"); // Prefix for messages from server to client
    }
}
