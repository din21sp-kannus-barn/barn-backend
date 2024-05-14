package com.vaisala.xweatherobserve.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;

@Controller
public class ImageController {

    @GetMapping("/weather-image.png")
    public ResponseEntity<byte[]> getWeatherImage() throws IOException {
        // Load the image file into a byte array
        byte[] imageBytes = Files.readAllBytes(Paths.get("weather-image.png"));

        // Return the byte array in the response
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
