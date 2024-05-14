package com.vaisala.xweatherobserve.model.dto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quality {
    private int qualityValue;
    private int checkLevel;
    private String checkedAt;
    private List<QualityReason> qualityReasons;
}
