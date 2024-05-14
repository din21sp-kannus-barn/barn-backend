package com.vaisala.xweatherobserve.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.vaisala.xweatherobserve.model.dto.ErrorRes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors()
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    
            ErrorRes errorRes = new ErrorRes(HttpStatus.BAD_REQUEST.value(), errors.toString(), "VALIDATION_ERROR", LocalDateTime.now()); 
    
            return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
        }
    
    
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorRes> handleAPIException(ApplicationException ex) {
        ErrorRes errorRes = new ErrorRes(ex.getStatus().value(), ex.getMessage(), "API_ERROR", LocalDateTime.now());

        return new ResponseEntity<>(errorRes, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRes> handleGlobalException(Exception ex) {
        ErrorRes errorRes = new ErrorRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), "INTERNAL_SERVER_ERROR", LocalDateTime.now());

        return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
