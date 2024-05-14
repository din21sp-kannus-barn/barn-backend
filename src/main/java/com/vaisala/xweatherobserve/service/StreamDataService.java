package com.vaisala.xweatherobserve.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import com.vaisala.xweatherobserve.exception.ApplicationException;
import com.vaisala.xweatherobserve.model.dto.WeatherSnapshot;
import com.vaisala.xweatherobserve.model.entity.ThermalSum;
import com.vaisala.xweatherobserve.respository.ThermalSumRepo;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;

// This service is responsible for handling weather updates and sending them to the client.
@Service
public class StreamDataService {

    // Message number counter
    private AtomicInteger messageNumber = new AtomicInteger(1);

    // Autowired SimpMessagingTemplate for sending messages
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ImageGenerator imageGenerator;

    // Autowired ThermalSumRepo for fetching the latest thermal sum from the database
    @Autowired
    private ThermalSumRepo thermalSumRepository;

    // Event listener for WeatherUpdateEvent
    @EventListener
    public void handleWeatherUpdateEvent(WeatherUpdateEvent event) {
        try {
            handleWeatherUpdate(event.getOurData());

        } catch (Exception e) {

            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to handle weather update: " + e.getMessage());
        }
    }

        // Method for handling weather updates
    public void handleWeatherUpdate(WeatherSnapshot ourdata) throws Exception {
        // Fetch the latest thermal sum from the database
        ThermalSum latestSum = thermalSumRepository.findTopByOrderByTimestampDesc();
        if (latestSum != null) {
            ourdata.setThermalSum(latestSum.getThermalSum());
        } else {
            // initialize ourdata's thermalSum to a default value
            ourdata.setThermalSum(0);
        }

        // Create a map to hold the payload
        Map<String, Object> payload = new HashMap<>();
        
        // Add the weather data, timestamp, and message number to the payload
        payload.put("weather data", ourdata);
        payload.put("timestamp", LocalDateTime.now());
        payload.put("messageNumber", messageNumber.getAndIncrement());
        
        // Send the payload as a message
        template.convertAndSend("/topic/weather", payload);

        // Generate an image from the WeatherSnapshot
        BufferedImage image = imageGenerator.generateImage(ourdata);

        // Write the image to a file
        File outputFile = new File("weather-image.png");
        ImageIO.write(image, "PNG", outputFile);
    }

}
