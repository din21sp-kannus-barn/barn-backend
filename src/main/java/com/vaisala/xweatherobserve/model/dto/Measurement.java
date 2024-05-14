package com.vaisala.xweatherobserve.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {
    private String sourceId;
    private String observedTime;
    private MeasurementType measurementType;
    private double value;
    private Quality quality;
    private String deviceId;

}
