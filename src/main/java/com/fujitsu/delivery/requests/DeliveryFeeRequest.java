package com.fujitsu.delivery.requests;

import jakarta.validation.constraints.NotBlank;

public class DeliveryFeeRequest {
    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    public DeliveryFeeRequest() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}

