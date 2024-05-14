package com.vaisala.xweatherobserve.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaisala.xweatherobserve.model.entity.ThermalSum;

public interface ThermalSumRepo extends JpaRepository<ThermalSum, Long> {

    ThermalSum findTopByOrderByTimestampDesc();
}
