package com.vaisala.xweatherobserve.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaisala.xweatherobserve.exception.ApplicationException;
import com.vaisala.xweatherobserve.model.constant.ParameterType;
import com.vaisala.xweatherobserve.model.dto.Measurement;
import com.vaisala.xweatherobserve.model.dto.WeatherSnapshot;
import com.vaisala.xweatherobserve.service.WeatherUpdateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;





@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    // Method called when a new WebSocket session is established
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        logger.info("New session established : {}", session.getId());
    }

    // Method called when a text message is received
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        logger.info("Received : {}", message.getPayload());
        try {
            // Convert the payload into an array of Measurement objects
            Measurement[] measurements = objectMapper.readValue(message.getPayload(), Measurement[].class);

            // Create a new OurData object from the measurements
            WeatherSnapshot ourdata = createOurDataFromMeasurements(measurements);

            // Publish a WeatherUpdateEvent
            eventPublisher.publishEvent(new WeatherUpdateEvent(this, ourdata));

            logger.info("Hajri : {}", ourdata);
            
        } catch (JsonProcessingException e) {

            logger.error("Error processing JSON", e);

            // Handle any exceptions that occur while processing the JSON
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JSON: " + e.getMessage());
        }
    }


    // Method to create a new OurData object from an array of measurements
    private WeatherSnapshot createOurDataFromMeasurements(Measurement[] measurements) {

        WeatherSnapshot ourdata = new WeatherSnapshot();

        for (Measurement measurement : measurements) {

            // Update the OurData object with the measurement
            updateOurData(measurement, ourdata);
        }
        return ourdata;
    }

    // Method to update an OurData object with a measurement
    private void updateOurData(Measurement measurement, WeatherSnapshot ourdata) {

        String parameter = measurement.getMeasurementType().getParameter();

        if (parameter.equalsIgnoreCase(ParameterType.AIR_TEMPERATURE)) {

            ourdata.setTemperature(measurement.getValue());

        } else if (parameter.equalsIgnoreCase(ParameterType.WIND_SPEED)) {

            ourdata.setWindSpeed(measurement.getValue());

        } else if (parameter.equalsIgnoreCase(ParameterType.WIND_DIRECTION)) {

            ourdata.setWindDirection(measurement.getValue());

        } else if (parameter.equalsIgnoreCase(ParameterType.RAIN_ACCUMULATION)) {

            ourdata.setRainAccumulation(measurement.getValue());
        }
    }

    // Method called when a transport error occurs
    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {

        logger.error("Transport error", exception);
        
        throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Transport error: " + exception.getMessage());
    }

    // Method called when a WebSocket session is closed
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {

        logger.info("Connection closed. Status: {}", status);
    }
}
