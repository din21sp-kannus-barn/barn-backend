package com.vaisala.xweatherobserve.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSnapshot {
    private double temperature;
    private double windSpeed;
    private double windDirection; 
    private double rainAccumulation;
    private double thermalSum;
    private double summerRainfall;

    @Override
    public String toString() {
        return "Weather{" +
            "temperature=" + temperature +
            ", windSpeed=" + windSpeed +
            ", windDirection=" + windDirection +
            ", rainAccumulation=" + rainAccumulation +
            ", thermalSum=" + thermalSum +  
            ", summerRainfall=" + summerRainfall +
            '}';
    }
}
