package com.vaisala.xweatherobserve.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementType {
    private String parameter;
    private String unit;
    private String statistic;
    private String period;
    private double height;
    private String dataLevel;
}
