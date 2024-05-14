package com.vaisala.xweatherobserve.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRes {
    private int status;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
}
