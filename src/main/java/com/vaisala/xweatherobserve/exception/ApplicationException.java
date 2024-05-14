package com.vaisala.xweatherobserve.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ApplicationException extends RuntimeException {

    private HttpStatus status;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
