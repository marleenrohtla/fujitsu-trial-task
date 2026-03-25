package com.fujitsu.delivery.service;

import com.fujitsu.delivery.entity.WeatherObservation;
import com.fujitsu.delivery.exception.ForbiddenVehicleException;
import com.fujitsu.delivery.repository.WeatherObservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class DeliveryFeeServiceTest {

    @Autowired
    private DeliveryFeeService deliveryFeeService;

    @Autowired
    private WeatherObservationRepository repository;

    //helper method to create test weather data
    private WeatherObservation createWeather(String station, double temp, double wind, String phenomenon) {
        WeatherObservation obs = new WeatherObservation();
        obs.setStationName(station);
        obs.setAirTemperature(temp);
        obs.setWindSpeed(wind);
        obs.setWeatherPhenomenon(phenomenon);
        obs.setTimestamp(LocalDateTime.now());
        return repository.save(obs);
    }

    @Test
    void testTallinnCarFee() {
        createWeather("Tallinn-Harku", -15, 25, "Heavy snow");
        double fee = deliveryFeeService.calculateFee("Tallinn", "Car");
        assertEquals(4.0, fee);
    }

    @Test
    void testForbiddenWindSpeed() {
        createWeather("Tallinn-Harku", 5, 25, "Clear");
        // Change RuntimeException to ForbiddenVehicleException
        assertThrows(ForbiddenVehicleException.class,
                () -> deliveryFeeService.calculateFee("Tallinn", "Bike"));
    }

    @Test
    void testForbiddenWeatherPhenomenon() {
        createWeather("Tallinn-Harku", 5, 5, "Glaze");
        // Change RuntimeException to ForbiddenVehicleException
        assertThrows(ForbiddenVehicleException.class,
                () -> deliveryFeeService.calculateFee("Tallinn", "Scooter"));
    }

    @Test
    void testTartuBikeExample() {
        createWeather("Tartu-Tõravere", -2.1, 4.7, "Light snow shower");
        double fee = deliveryFeeService.calculateFee("Tartu", "Bike");
        assertEquals(4.0, fee);
    }
}
