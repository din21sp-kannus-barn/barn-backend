package com.vaisala.xweatherobserve.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.vaisala.xweatherobserve.model.dto.Measurement;

@Service
public class HistoricalWeatherApiService {

    @Value("${WEATHER_API_URL}")
    private String apiUrl;

    @Value("${WEATHER_HISTORY_ENDPOINT}")
    private String historyEndpoint;

    @Value("${SENSOR_ID}")
    private String sensorId;

    @Value("${WEATHER_API_KEY}")
    private String apiKey;



    /**
     * Fetches measurements from the historical API.
     *
     * @param startTime the start time for the measurement period.
     * @param endTime   the end time for the measurement period.
     * @param parameter the measurement parameter to filter by.
     * @return an array of {@link Measurement} objects.
     */
    public Measurement[] fetchMeasurements(String startTime, String endTime) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
            .path(historyEndpoint + sensorId)
            .queryParam("start_time", startTime)
            .queryParam("end_time", endTime)
            .build().encode().toUriString();
            
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("X-API-Key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Measurement[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Measurement[].class);
        return response.getBody();
    }
}
