package com.fujitsu.delivery.controller;

import com.fujitsu.delivery.dto.DeliveryFeeResponse;
import com.fujitsu.delivery.service.DeliveryFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/delivery-fee")
public class DeliveryFeeController {
    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    /**
     * Calculates the delivery fee based on city and vehicle type
     * @param city - Tallinn, Tartu or Pärnu
     * @param vehicleType - Car, Scooter or Bike
     * @return total delivery fee or error message
     */

    @GetMapping
    public ResponseEntity<DeliveryFeeResponse> getDeliveryFee(
            @RequestParam String city,
            @RequestParam String vehicleType) {
        try{
            //calculates the fee using the service
            java.math.BigDecimal fee = deliveryFeeService.calculateFee(city, vehicleType);

            //returns 200 OK with the fee in the response body
            return ResponseEntity.ok(new DeliveryFeeResponse(fee));

        } catch (RuntimeException e) {
            //returns 400 Bad Request with error message if vehicle is forbidden or city/vehicle is unknown
            return ResponseEntity.badRequest().body(new DeliveryFeeResponse(e.getMessage()));
        }

    }
}
