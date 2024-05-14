package com.vaisala.xweatherobserve.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;

import com.vaisala.xweatherobserve.exception.ApplicationException;
import com.vaisala.xweatherobserve.handler.WebSocketHandler;
import java.util.concurrent.CompletableFuture;
import java.io.IOException;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import jakarta.annotation.PostConstruct;
import java.net.URI;

// This service is responsible for managing the WebSocket connection.
@Service
public class RealTimeWeatherApiService {

    // WebSocket session
    private WebSocketSession session;

    @Value("${WEATHER_URL}")
    private String apiUrl;

    @Value("${WEATHER_API_KEY}")
    private String apiKey;

    // Autowired WebSocketHandler
    @Autowired
    private WebSocketHandler webSocketHandler;

    // Connect to the WebSocket API when the service is initialized
    @PostConstruct
    public void connect() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = getHeaders();

        CompletableFuture<WebSocketSession> future = client.execute(webSocketHandler, headers, URI.create(apiUrl));

        future.thenAccept(session -> {
            this.session = session;
            System.out.println("Successfully joined session");
        }).exceptionally(ex -> {
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to connect to Vaisala WebSocket API: " + ex.getMessage());
        });
    }

    // Create headers for the WebSocket connection
    private WebSocketHttpHeaders getHeaders() {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("x-api-key", apiKey);
        return headers;
    }

    // Disconnect from the WebSocket API
    public synchronized void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                session = null;
            } catch (IOException e) {
                throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to close WebSocketSession: " + e.getMessage());
            }
        }
    }    
}
