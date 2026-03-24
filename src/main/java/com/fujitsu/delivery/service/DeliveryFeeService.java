package com.fujitsu.delivery.service;

import com.fujitsu.delivery.entity.WeatherObservation;
import com.fujitsu.delivery.repository.WeatherObservationRepository;
import org.springframework.stereotype.Service;

@Service
public class DeliveryFeeService {

    private final WeatherObservationRepository repository;

    public DeliveryFeeService(WeatherObservationRepository repository) {
        this.repository = repository;
    }

    /**
    * calculates the total delivery fee based on city, vehicle type and latest weather data
     * @param city - Tallinn, Tartu or Pärnu
     * @param vehicleType - Car, Scooter or Bike
     * @return total delivery fee in euros
     */

     public double calculateFee(String city, String vehicleType) {

         //get the station name for the given city
         String stationName = getStationName(city);

         //get the latest weather data for that station
         WeatherObservation weather = repository
                 .findTopByStationNameOrderByTimestampDesc(stationName)
                 .orElseThrow(() -> new RuntimeException("No weather data found for " + city));

         //calculates all fee components
         double rbf = calculateRBF(city, vehicleType);
         double atef = calculateATEF(vehicleType, weather.getAirTemperature());
         double wsef = calculateWSEF(vehicleType, weather.getWindSpeed());
         double wpef = calculateWPEF(vehicleType, weather.getWeatherPhenomenon());

         return rbf + atef + wsef + wpef;
     }

    /**
     * Maps city name to weather station name
     */
    private String getStationName(String city) {
        return switch (city.toLowerCase()) {
            case "tallinn" -> "Tallinn-Harku";
            case "tartu" -> "Tartu-Tõravere";
            case "pärnu" -> "Pärnu";
            default -> throw new RuntimeException("Unknow city: " + city);
        };
    }

    /**
     * Calculates Regional Base Fee based on city and vehicle type
     */
    private double calculateRBF(String city, String vehicleType) {
        return switch (city.toLowerCase()) {
            case "tallinn" -> switch (vehicleType.toLowerCase()) {
                case "car" -> 4.0;
                case "scooter" -> 3.5;
                case "bike" -> 3.0;
                default -> throw new RuntimeException("Unknown vehicle type: " + vehicleType);
            };
            case "tartu" -> switch (vehicleType.toLowerCase()) {
                case "car" -> 3.5;
                case "scooter" -> 3.0;
                case "bike" -> 2.5;
                default -> throw new RuntimeException("Unknown vehicle type: " + vehicleType);
            };
            case "pärnu" -> switch (vehicleType.toLowerCase()) {
                case "car" -> 3.0;
                case "scooter" -> 2.5;
                case "bike" -> 2.0;
                default -> throw new RuntimeException("Unknown vehicle type: " + vehicleType);
            };
            default -> throw new RuntimeException("Unknown city: " + city);
        };
    }

    /**
     * Calculates Air Temperature Extra Fee
     */
    private double calculateATEF(String vehicleType, Double temperature) {
        //applies only to scooter and bike
        if (temperature == null)
            return 0;
        if (vehicleType.equalsIgnoreCase("car"))
            return 0;

        if (temperature < -10)
            return 1.0; //below -10 then ATEF 1€
        if (temperature <= 0)
            return 0.5; //between -10 and 0 then ATEF 0.5€
        return 0; //above 0 then ATEF 0€
    }

    /**
     * Calculates Wind Speed Extra Fee
     */
    private double calculateWSEF(String vehicleType, Double windSpeed) {
        //applies only to bikes, if not a bike return 0
        if (windSpeed == null)
            return 0;
        if (!vehicleType.equalsIgnoreCase("bike"))
            return 0;

        // wind stronger than 20 m/s is too dangerous for a bike - throws error
        if (windSpeed > 20)
            throw new RuntimeException("Usage of selected vehicle type is forbidden");
        // wind between 10 and 20 m/s - add 0.5€ extra fee
        if (windSpeed >= 10)
            return 0.5;
        // wind below 10 m/s - no extra fee
        return 0;
    }

    /**
     * Calculates Weather Phenomenon Extra Fee
     */
    private double calculateWPEF (String vehicleType, String phenomenon) {
        // applies only to scooters and bikes
        if (phenomenon == null || phenomenon.isEmpty())
            return 0;
        if(vehicleType.equalsIgnoreCase("car"))
            return 0;

        String p = phenomenon.toLowerCase();

        // if weather is glaze, hail or thunder - it is too dangerous for scooter or bike
        // if any of these are true, throw an error
        if (p.contains("glaze") || p.contains("hail") || p.contains("thunder"))
            throw new RuntimeException("Usage of selected vehicle type is forbidden");

        // if weather contains snow or sleet - add 1€ extra fee
        if (p.contains("snow") || p.contains("sleet"))
            return 1.0;

        // if weather contains rain - add 0.5€ extra fee
        if (p.contains("rain"))
            return 0.5;

        return 0;
    }

}
