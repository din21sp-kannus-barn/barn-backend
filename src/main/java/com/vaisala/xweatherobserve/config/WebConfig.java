package com.vaisala.xweatherobserve.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Configure Cross-Origin Resource Sharing (CORS)
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**") // Allow requests to all endpoints
                .allowedOriginPatterns("http://localhost:3000", "https://barnprojectjava.azurewebsites.net", "*") // Allow requests from this origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // Allow these HTTP methods
                .allowedHeaders("*") 
                .allowCredentials(true); 
    }
}
