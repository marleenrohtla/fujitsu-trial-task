package com.fujitsu.delivery.service;

import com.fujitsu.delivery.entity.WeatherObservation;
import com.fujitsu.delivery.exception.ForbiddenVehicleException;
import com.fujitsu.delivery.repository.WeatherObservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
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
    private WeatherObservation createWeather(String station, BigDecimal temp, BigDecimal wind, String phenomenon) {
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
        createWeather("Tallinn-Harku", new BigDecimal("-15"),new BigDecimal("25"), "Heavy snow");
        BigDecimal fee = deliveryFeeService.calculateFee("Tallinn", "Car");
        assertEquals(new BigDecimal("4.0"), fee);
    }

    @Test
    void testTallinnBikeWithSnowAndCold() {
        createWeather("Tallinn-Harku", new BigDecimal("-5"), new BigDecimal("8"), "Light snow shower");
        BigDecimal fee = deliveryFeeService.calculateFee("Tallinn", "Bike");
        assertEquals(new BigDecimal("4.5"), fee);
    }

    @Test
    void testForbiddenWindSpeed() {
        createWeather("Tallinn-Harku", new BigDecimal("5"),new BigDecimal("25"), "Clear");
        // Change RuntimeException to ForbiddenVehicleException
        assertThrows(ForbiddenVehicleException.class,
                () -> deliveryFeeService.calculateFee("Tallinn", "Bike"));
    }

    @Test
    void testForbiddenWeatherPhenomenon() {
        createWeather("Tallinn-Harku", new BigDecimal("5"),new BigDecimal("5"), "Glaze");
        // Change RuntimeException to ForbiddenVehicleException
        assertThrows(ForbiddenVehicleException.class,
                () -> deliveryFeeService.calculateFee("Tallinn", "Scooter"));
    }

    @Test
    void testTartuBikeExample() {
        createWeather("Tartu-Tõravere", new BigDecimal("-2.1"), new BigDecimal("4.7"), "Light snow shower");
        BigDecimal fee = deliveryFeeService.calculateFee("Tartu", "Bike");
        assertEquals(new BigDecimal("4.0"), fee);
    }
}
