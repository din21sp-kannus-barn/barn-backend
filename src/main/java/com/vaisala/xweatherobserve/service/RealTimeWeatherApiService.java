package com.vaisala.xweatherobserve.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;

import com.vaisala.xweatherobserve.exception.ApplicationException;
import com.vaisala.xweatherobserve.handler.WebSocketHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.io.IOException;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import org.springframework.web.socket.TextMessage;

import java.util.concurrent.TimeUnit;

// This service is responsible for managing the WebSocket connection.
@Service
public class RealTimeWeatherApiService {

    // Maximum number of reconnection attempts
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    // Counter for the number of reconnection attempts
    private int reconnectAttempts = 0;

    // Message to be sent as a heartbeat
    private static final String HEARTBEAT_MESSAGE = "heartbeat";

    // Logger instance
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    // Executor service for scheduling tasks
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // WebSocket session
    private WebSocketSession session;

    // URL for the WebSocket API, injected from application properties
    @Value("${WEATHER_URL}")
    private String apiUrl;

    // API key for the WebSocket API, injected from application properties
    @Value("${WEATHER_API_KEY}")
    private String apiKey;

    // Autowired WebSocketHandler
    @Autowired
    private WebSocketHandler webSocketHandler;

    // Connect to the WebSocket API when the service is initialized
    @PostConstruct
    public void connect() {
        // Create a new WebSocket client
        WebSocketClient client = new StandardWebSocketClient();
        // Get the headers for the WebSocket connection
        WebSocketHttpHeaders headers = getHeaders();

        // Execute the WebSocket handshake
        CompletableFuture<WebSocketSession> future = client.execute(webSocketHandler, headers, URI.create(apiUrl));

        // When the handshake is complete, start the heartbeat and log the successful connection
        future.thenAccept(session -> {
            this.session = session;
            startHeartbeat();
            System.out.println("Successfully joined session");
        }).exceptionally(ex -> {
            // If the handshake fails, throw an ApplicationException
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to connect to Vaisala WebSocket API: " + ex.getMessage());
        });
    }

    // Create headers for the WebSocket connection
    private WebSocketHttpHeaders getHeaders() {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        // Add the API key to the headers
        headers.add("x-api-key", apiKey);
        return headers;
    }

    // Disconnect from the WebSocket API
    public synchronized void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                // Close the WebSocket session
                session.close();
                session = null;
            } catch (IOException e) {
                // If an error occurs while closing the session, throw an ApplicationException
                throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to close WebSocketSession: " + e.getMessage());
            }
        }
    }   

    // Start sending heartbeat messages
    private void startHeartbeat() {
        // Schedule a task to send a heartbeat message every 30 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (session != null && session.isOpen()) {
                    // Send a heartbeat message
                    session.sendMessage(new TextMessage(HEARTBEAT_MESSAGE));
                }
            } catch (IOException e) {
                // If an error occurs while sending the heartbeat message, throw an ApplicationException
                throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send heartbeat message: " + e.getMessage());
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    // Handle the WebSocket connection being closed
    public void handleConnectionClosed(CloseStatus status) {
        logger.info("Connection closed. Status: {}", status);
        if ( reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            // If the maximum number of reconnection attempts has not been reached, try to reconnect
            try {
                Thread.sleep(5000);  // Wait for 5 seconds before attempting to reconnect
                connect();  // Call the connect() method from RealTimeWeatherApiService
                reconnectAttempts++;
            } catch (InterruptedException e) {
                logger.error("Failed to sleep before reconnecting", e);
            }
        }
    }
}
