package com.vaisala.xweatherobserve.service;

import org.springframework.context.ApplicationEvent;

import com.vaisala.xweatherobserve.model.dto.WeatherSnapshot;

// This event is published when a new message is received from the WebSocket API.
public class WeatherUpdateEvent extends ApplicationEvent {

    private WeatherSnapshot ourdata;

    // Constructor for the event. It takes the source of the event and the OurData object.
    public WeatherUpdateEvent(Object source, WeatherSnapshot ourdata) {
        super(source);
        this.ourdata = ourdata;
    }

    // Getter for the OurData object.
    public WeatherSnapshot getOurData() {
        return ourdata;
    }
}
