package com.fujitsu.delivery.service;

import com.fujitsu.delivery.entity.WeatherObservation;
import com.fujitsu.delivery.repository.WeatherObservationRepository;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final WeatherObservationRepository repository;

    public WeatherService(WeatherObservationRepository repository) {
        this.repository = repository;
    }

    /** gets the latest weather observation for a given station
     * @param stationName = name of the weather station
     * @return latest WeatherObservation for that station
     */

    public WeatherObservation getLatestWeather(String stationName) {
        return repository
                .findTopByStationNameOrderByTimestampDesc(stationName)
                .orElseThrow(() -> new RuntimeException("No weather data found for station: " + stationName));
    }
}
