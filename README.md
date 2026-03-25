# Fujitsu Delivery Fee Calculator

A Spring Boot application that calculates delivery fees for food couriers based on regional base fee, vehicle type, and weather conditions.

## Technologies
- Java 21
- Spring Boot 4.0.4
- H2 Database
- Gradle

## How to run
1. Clone the repository
2. Run the application:
```
./gradlew bootRun
```
3. The application starts at `http://localhost:8080`

## API Usage
Calculate delivery fee:
```
GET http://localhost:8080/api/delivery-fee?city=Tallinn&vehicleType=Bike
```

**Cities:** Tallinn, Tartu, Pärnu

**Vehicle types:** Car, Scooter, Bike

**Example response:**
```json
{
    "fee": 4.5,
    "errorMessage": null
}
```

**Error response:**
```json
{
    "fee": null,
    "errorMessage": "Usage of selected vehicle type is forbidden"
}
```

## Fee calculation
Total fee = Regional Base Fee + Air Temperature Extra Fee + Wind Speed Extra Fee + Weather Phenomenon Extra Fee

## Weather data
Weather data is automatically imported from the Estonian Environment Agency every hour at HH:15:00 from:
https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php

## H2 Database Console
Available at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:deliverydb`
- Username: `SA`
- Password: (empty)

## Testing the API manually

Since weather data is imported automatically every hour, you can insert test data manually in the H2 console to test the API immediately.

Go to `http://localhost:8080/h2-console` and run these SQL inserts:
```sql
-- Tallinn
INSERT INTO WEATHER_OBSERVATION (STATION_NAME, WMO_CODE, AIR_TEMPERATURE, WIND_SPEED, WEATHER_PHENOMENON, TIMESTAMP)
VALUES ('Tallinn-Harku', '26038', -5.0, 8.0, 'Light snow shower', CURRENT_TIMESTAMP);

-- Tartu
INSERT INTO WEATHER_OBSERVATION (STATION_NAME, WMO_CODE, AIR_TEMPERATURE, WIND_SPEED, WEATHER_PHENOMENON, TIMESTAMP)
VALUES ('Tartu-Tõravere', '26242', -2.1, 4.7, 'Light snow shower', CURRENT_TIMESTAMP);

-- Pärnu
INSERT INTO WEATHER_OBSERVATION (STATION_NAME, WMO_CODE, AIR_TEMPERATURE, WIND_SPEED, WEATHER_PHENOMENON, TIMESTAMP)
VALUES ('Pärnu', '41803', 0.5, 5.0, 'Clear', CURRENT_TIMESTAMP);
```

Then test in Postman:
```
GET http://localhost:8080/api/delivery-fee?city=Tallinn&vehicleType=Bike
GET http://localhost:8080/api/delivery-fee?city=Tartu&vehicleType=Scooter
GET http://localhost:8080/api/delivery-fee?city=Pärnu&vehicleType=Car
```

## Running tests
```
./gradlew test
```