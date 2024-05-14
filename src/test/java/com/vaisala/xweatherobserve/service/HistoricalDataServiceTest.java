package com.vaisala.xweatherobserve.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaisala.xweatherobserve.model.dto.Measurement;
import com.vaisala.xweatherobserve.model.dto.MeasurementType;
import com.vaisala.xweatherobserve.model.dto.Quality;
import com.vaisala.xweatherobserve.model.entity.ThermalSum;
import com.vaisala.xweatherobserve.respository.ThermalSumRepo;

@SpringBootTest
public class HistoricalDataServiceTest {

    @MockBean
    private ThermalSumRepo thermalSumRepo;

    @MockBean
    private HistoricalWeatherApiService weatherApiClient;

    @InjectMocks
    private HistoricalDataService historicalDataService;

    @Value("${WEATHER_API_URL}")
    private String apiUrl;

    @Value("${WEATHER_HISTORY_ENDPOINT}")
    private String historyEndpoint;

    @Value("${SENSOR_ID}")
    private String sensorId;

    @Value("${WEATHER_API_KEY}")
    private String apiKey;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(historicalDataService, "apiUrl", apiUrl);
        ReflectionTestUtils.setField(historicalDataService, "historyEndpoint", historyEndpoint);
        ReflectionTestUtils.setField(historicalDataService, "sensorId", sensorId);
        ReflectionTestUtils.setField(historicalDataService, "apiKey", apiKey);
    }
    

    

    private void setupMocks(Measurement[] measurements, ThermalSum latestThermalSum) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        String endTime = nowUtc.format(formatter);
        String startTime = nowUtc.minusHours(6).format(formatter);
    
        // when(historicalDataService.fetchMeasurements(startTime, endTime)).thenReturn(measurements);
        when(weatherApiClient.fetchMeasurements(startTime, endTime)).thenReturn(measurements);
        when(thermalSumRepo.findTopByOrderByTimestampDesc()).thenReturn(latestThermalSum);
    }

    // Test scenario where temperatures are above the BASE_TEMPERATURE for 10 consecutive days
    @Test
    public void testCalculateAndStoreThermalSum_TemperaturesAboveBaseForTenDays() {
        // Arrange
        Measurement[] measurements = createMeasurements(new double[]{6.0, 6.0, 6.0, 6.0});
        ThermalSum latestThermalSum = new ThermalSum();
        latestThermalSum.setThermalSum(0);
        setupMocks(measurements, latestThermalSum);

        // Act and Assert
        for (int i = 0; i < 10; i++) {
            historicalDataService.calculateAndStoreThermalSum();
            verify(thermalSumRepo, times(0)).save(any(ThermalSum.class));
        }

        // Now we should have had 10 consecutive days with temperatures above the base temperature
        // So the next call should calculate and store the thermal sum
        historicalDataService.calculateAndStoreThermalSum();
        verifySaveCalledWithThermalSum(1.0);
    }
    
    // Test scenario where all temperatures are above the BASE_TEMPERATURE
    @Test
    public void testCalculateAndStoreThermalSum_HappyPath() {
        // Arrange
        Measurement[] measurements = createMeasurements(new double[]{6.8, 6.0});
        setupMocks(measurements, new ThermalSum());



        // Act
        historicalDataService.calculateAndStoreThermalSum();

        // Assert
        verifySaveCalledWithThermalSum(1.8);
    }

    // Test scenario where all temperatures are below the BASE_TEMPERATURE
    @Test
    public void testCalculateAndStoreThermalSum_AllTemperaturesBelowBase() {
        // Arrange
        Measurement[] measurements = createMeasurements(new double[]{4.0});
        setupMocks(measurements, new ThermalSum());

        // Act
        historicalDataService.calculateAndStoreThermalSum();

        // Assert
        verify(thermalSumRepo, times(0)).save(any(ThermalSum.class));
    }

    // Test scenario where all temperatures are below the THRESHOLD_TEMPERATURE
    @Test
    public void testCalculateAndStoreThermalSum_AllTemperaturesBelowThreshold() {
        // Arrange
        Measurement[] measurements = createMeasurements(new double[]{3.0});
        setupMocks(measurements, new ThermalSum());

        // Act
        historicalDataService.calculateAndStoreThermalSum();

        // Assert
        verify(thermalSumRepo, times(0)).save(any(ThermalSum.class));
    }

    // Test scenario where there are no measurements
    @Test
    public void testCalculateAndStoreThermalSum_NoMeasurements() {
        // Arrange
        Measurement[] measurements = new Measurement[] {};
        setupMocks(measurements, new ThermalSum());

        // Act
        historicalDataService.calculateAndStoreThermalSum();

        // Assert
        verify(thermalSumRepo, times(0)).save(any(ThermalSum.class));
    }

    private Measurement[] createMeasurements(double[] temperatures) {
        return Arrays.stream(temperatures)
            .mapToObj(temp -> new Measurement("sourceId1", "2024-04-29T11:27:00Z",
                new MeasurementType("AIR_TEMPERATURE", "DEGREES_CELSIUS", "MEAN", "PT1M", 0.0, "LEVEL_TWO"),
                temp, new Quality(8500, 2, "2024-04-29T11:27:00Z", new ArrayList<>()),
                "deviceId1"))
            .toArray(Measurement[]::new);
    }

    private void verifySaveCalledWithThermalSum(double expectedThermalSum) {
        ArgumentCaptor<ThermalSum> argumentCaptor = ArgumentCaptor.forClass(ThermalSum.class);
        verify(thermalSumRepo, times(1)).save(argumentCaptor.capture());
        ThermalSum savedThermalSum = argumentCaptor.getValue();
        assertEquals(expectedThermalSum, savedThermalSum.getThermalSum(), 0.01);
    }


}
