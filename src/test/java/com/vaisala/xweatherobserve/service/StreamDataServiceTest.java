package com.vaisala.xweatherobserve.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import static org.mockito.Mockito.*;


import com.vaisala.xweatherobserve.model.dto.WeatherSnapshot;
import com.vaisala.xweatherobserve.model.entity.ThermalSum;
import com.vaisala.xweatherobserve.respository.ThermalSumRepo;


@SpringBootTest
public class StreamDataServiceTest {

    @MockBean
    private SimpMessagingTemplate template;

    @MockBean
    private ThermalSumRepo thermalSumRepo;

    @Autowired
    private StreamDataService streamDataService;

    @Test
    public void testHandleWeatherUpdate() throws Exception {
        // Arrange
        WeatherSnapshot weatherSnapshot = new WeatherSnapshot();
        ThermalSum latestThermalSum = new ThermalSum();
        latestThermalSum.setThermalSum(100.0);

        when(thermalSumRepo.findTopByOrderByTimestampDesc()).thenReturn(latestThermalSum);

        // Act
        streamDataService.handleWeatherUpdate(weatherSnapshot);

        // Assert
        verify(template, times(1)).convertAndSend(anyString(), anyMap());
    }
}
