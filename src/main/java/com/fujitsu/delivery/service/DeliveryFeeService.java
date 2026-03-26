package com.fujitsu.delivery.service;

import com.fujitsu.delivery.entity.WeatherObservation;
import com.fujitsu.delivery.enums.City;
import com.fujitsu.delivery.enums.VehicleType;
import com.fujitsu.delivery.enums.WeatherPhenomenon;
import com.fujitsu.delivery.exception.ForbiddenVehicleException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DeliveryFeeService {

    private final WeatherService weatherService;

    public DeliveryFeeService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
    * calculates the total delivery fee based on city, vehicle type and latest weather data
     * @param city - Tallinn, Tartu or Pärnu
     * @param vehicleType - Car, Scooter or Bike
     * @return total delivery fee in euros
     */

     public BigDecimal calculateFee(String city, String vehicleType) {

         //converts string inputs to enums
         City parsedCity = City.from(city);
         VehicleType parsedVehicleType = VehicleType.from(vehicleType);

         //get the latest station name for the given city
         String stationName = parsedCity.getStationName();

         //get the latest weather data for that station
         WeatherObservation weather = weatherService.getLatestWeather(stationName);

         //calculates all fee components
         BigDecimal RegionalBaseFee = calculateRegionalBaseFee(parsedCity, parsedVehicleType);
         BigDecimal AirTemperatureExtraFee = calculateAirTemperatureExtraFee(parsedVehicleType, weather.getAirTemperature());
         BigDecimal WindSpeedExtraFee = calculateWindSpeedExtraFee(parsedVehicleType, weather.getWindSpeed());
         BigDecimal WeatherPhenomenonExtraFee = calculateWeatherPhenomenonExtraFee(parsedVehicleType, weather.getWeatherPhenomenon());

         return RegionalBaseFee.add(AirTemperatureExtraFee).add(WindSpeedExtraFee).add(WeatherPhenomenonExtraFee);
     }

    /**
     * Calculates Regional Base Fee based on city and vehicle type
     */
    private BigDecimal calculateRegionalBaseFee(City city, VehicleType vehicleType) {
        return switch (city) {
            case TALLINN -> switch (vehicleType) {
                case CAR -> new BigDecimal("4.0");
                case SCOOTER -> new BigDecimal("3.5");
                case BIKE -> new BigDecimal("3.0");
            };
            case TARTU -> switch (vehicleType) {
                case CAR -> new BigDecimal("3.5");
                case SCOOTER -> new BigDecimal("3.0");
                case BIKE -> new BigDecimal("2.5");
            };
            case PÄRNU -> switch (vehicleType) {
                case CAR -> new BigDecimal("3.0");
                case SCOOTER -> new BigDecimal("2.5");
                case BIKE -> new BigDecimal("2.0");
            };
        };
    }

    /**
     * Calculates Air Temperature Extra Fee
     */
    private BigDecimal calculateAirTemperatureExtraFee(VehicleType vehicleType, BigDecimal temperature) {
        //applies only to scooter and bike
        if (temperature == null) {
            return BigDecimal.ZERO;
        }
        if (vehicleType == vehicleType.CAR) {
            return BigDecimal.ZERO;
        }

        if (temperature.compareTo(new BigDecimal("-10")) < 0) {
            return new BigDecimal("1.0"); //below -10 then ATEF 1€
        }
        if (temperature.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("0.5"); //between -10 and 0 then ATEF 0.5€
        }
        return BigDecimal.ZERO; //above 0 then ATEF 0€
    }

    /**
     * Calculates Wind Speed Extra Fee
     */
    private BigDecimal calculateWindSpeedExtraFee(VehicleType vehicleType, BigDecimal windSpeed) {
        //applies only to bikes, if not a bike return 0
        if (windSpeed == null) {
            return BigDecimal.ZERO;
        }
        if (vehicleType != VehicleType.BIKE) {
            return BigDecimal.ZERO;
        }

        // wind stronger than 20 m/s is too dangerous for a bike - throws error
        if (windSpeed.compareTo(new BigDecimal("20")) > 0) {
            throw new ForbiddenVehicleException();
        }
        // wind between 10 and 20 m/s - add 0.5€ extra fee
        if (windSpeed.compareTo(new BigDecimal("10")) >= 0) {
            return new BigDecimal("0.5");
        }
        // wind below 10 m/s - no extra fee
        return BigDecimal.ZERO;
    }

    /**
     * Calculates Weather Phenomenon Extra Fee
     */
    private BigDecimal calculateWeatherPhenomenonExtraFee (VehicleType vehicleType, String phenomenon) {
        // applies only to scooters and bikes
        if (phenomenon == null || phenomenon.isEmpty()) {
            return BigDecimal.ZERO;
        }
        if(vehicleType == VehicleType.CAR) {
            return BigDecimal.ZERO;
        }

        // find the matching weather phenomenon from the text
        // and return the appropriate fee based on its behavior
        return WeatherPhenomenon.fromText(phenomenon)

               //get the behavior of the matched phenomenon (FORBIDDEN, HIGH_FEE, LOW_FEE)
                .map(WeatherPhenomenon::getBehavior)
                //map the behaviour to a fee amout
                .map(behavior -> switch (behavior) {
                    //if forbidden - throw exception, vehicle cannot be used
                    case FORBIDDEN -> throw new ForbiddenVehicleException();
                    //if high fee - add 1€ extra fee (snow or sleet)
                    case HIGH_FEE -> new BigDecimal("1.0");
                    //if low fee - add 0.5€ extra fee (rain)
                    case LOW_FEE -> new BigDecimal("0.5");
                })

                //if no matching phenomenon found - no extra fee
                .orElse(BigDecimal.ZERO);
    }

}
