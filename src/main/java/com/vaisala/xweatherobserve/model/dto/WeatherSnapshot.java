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

    public void setTemperature(double temperature) {
        this.temperature = (int) temperature;
    }

    public void setRainAccumulation(double rainAccumulation) {
        this.rainAccumulation = Math.round(rainAccumulation * 100.0) / 100.0;
    }

    public void setThermalSum(double thermalSum) {
        this.thermalSum = Math.round(thermalSum * 10.0) / 10.0;
    }

    @Override
    public String toString() {
        return "Weather{" +
            "temperature=" + (int)temperature +
            ", windSpeed=" + windSpeed +
            ", windDirection=" + windDirection +
            ", rainAccumulation=" + String.format("%.02f", rainAccumulation) +
            ", thermalSum=" + thermalSum +  
            ", summerRainfall=" + summerRainfall +
            '}';
    }
}
