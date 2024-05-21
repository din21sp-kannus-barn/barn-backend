package com.vaisala.xweatherobserve.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.vaisala.xweatherobserve.model.dto.Measurement;
import com.vaisala.xweatherobserve.model.entity.ThermalSum;
import com.vaisala.xweatherobserve.respository.ThermalSumRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for calculating and storing thermal sum and summer rainfall data.
 */
@Service
public class HistoricalDataService {

    private static final Logger logger = LoggerFactory.getLogger(HistoricalDataService.class);
    private static final double BASE_TEMPERATURE = 5.0;
    private int consecutiveDaysAboveBaseTemp = 0;

    // A list to store the daily average temperatures
    private List<Double> dailyAvgTemperatures = new ArrayList<>();

    @Autowired
    private ThermalSumRepo thermalSumRepo;

    @Autowired
    private HistoricalWeatherApiService weatherApiClient;


    @Value("${WEATHER_API_URL}")
    private String apiUrl;

    @Value("${WEATHER_HISTORY_ENDPOINT}")
    private String historyEndpoint;

    @Value("${SENSOR_ID}")
    private String sensorId;

    @Value("${WEATHER_API_KEY}")
    private String apiKey;



    /**
     * Scheduled task to calculate and store the thermal sum every 6 hours.
     */
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000, initialDelay = 0)
    public void calculateAndStoreThermalSum() {
        // Prepare the formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
        // Get current time in UTC
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);

        //from the past 6 hours from the time of fetching
        String endTime = nowUtc.format(formatter);
        String startTime = nowUtc.minusHours(6).format(formatter);
    
    
        try {
            // Measurement[] measurements = fetchMeasurements(startTime, endTime);
            Measurement[] measurements = weatherApiClient.fetchMeasurements(startTime, endTime);
            List<Measurement> temperatureMeasurements = filterMeasurementsByType(measurements, "AIR_TEMPERATURE");
    
            double dailyAverageTemperature = temperatureMeasurements.stream()
                .mapToDouble(Measurement::getValue)
                .average()
                .orElse(Double.NaN);
    
             // Store the daily average temperature
    dailyAvgTemperatures.add(dailyAverageTemperature);

    // If we have collected four 6-hour averages, calculate the daily average
    if (dailyAvgTemperatures.size() == 4) {
        double trueDailyAverage = dailyAvgTemperatures.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(Double.NaN);

        logger.info("Calculated true daily average temperature: {}", trueDailyAverage);


        // Reset the list for the next day
        dailyAvgTemperatures.clear();

        if (trueDailyAverage > BASE_TEMPERATURE) {
            consecutiveDaysAboveBaseTemp++;
        } else {
            consecutiveDaysAboveBaseTemp = 0;
        }

        if (consecutiveDaysAboveBaseTemp >= 10) {
                double thermalSum = dailyAverageTemperature - BASE_TEMPERATURE;

                logger.info("Thermal sum to be saved: {}", thermalSum);

    
                ThermalSum latestWeatherData = thermalSumRepo.findTopByOrderByTimestampDesc();
                if (latestWeatherData == null) {
                    latestWeatherData = new ThermalSum();
                    latestWeatherData.setThermalSum(0);
                }
                latestWeatherData.setThermalSum(latestWeatherData.getThermalSum() + thermalSum);
                thermalSumRepo.save(latestWeatherData);
    
                logger.info("Calculated and stored thermal sum: {}", thermalSum);
            }
        }
        } catch (Exception e) {
            logger.error("Failed to calculate or store thermal sum: {}", e.getMessage(), e);
        }
    
    }
    

    /**
     * Calculates and stores total summer rainfall on September 1st each year.
     */
    @Scheduled(cron = "0 0 0 1 9 ?")
    public void calculateAndStoreSummerRainfall() {
        LocalDate now = LocalDate.now();
        String startTime = LocalDate.of(now.getYear(), 6, 1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endTime = LocalDate.of(now.getYear(), 8, 31).format(DateTimeFormatter.ISO_LOCAL_DATE);

        try {
            // Measurement[] measurements = fetchMeasurements(startTime, endTime);
            Measurement[] measurements = weatherApiClient.fetchMeasurements(startTime, endTime);
            double summerRainfall  = filterMeasurementsByType(measurements, "RAIN_ACCUMULATION").stream()
                .mapToDouble(Measurement::getValue)
                .sum();

            ThermalSum thermalSum = new ThermalSum();
            thermalSum.setTimestamp(LocalDateTime.now());
            thermalSum.setSummerRainfall(summerRainfall);
            thermalSumRepo.save(thermalSum);

            logger.info("Calculated and stored summer rainfall: {}", summerRainfall);
        } catch (Exception e) {
            logger.error("Failed to calculate or store summer rainfall: {}", e.getMessage(), e);
        }
    }

    /**
     * Filters an array of {@link Measurement} objects by parameter.
     *
     * @param measurements the array of measurements to filter.
     * @param parameter    the parameter to filter by.
     * @return a list of {@link Measurement} objects with the specified parameter.
     */
    private List<Measurement> filterMeasurementsByType(Measurement[] measurements, String type) {
        return Arrays.stream(measurements)
                .filter(m -> type.equals(m.getMeasurementType().getParameter()))
                .collect(Collectors.toList());
    }
    
}
