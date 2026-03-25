package com.fujitsu.delivery.dto;

public class DeliveryFeeResponse {

    private Double fee;
    private String errorMessage;

    //constructor for successful response
    public DeliveryFeeResponse (Double fee) {
        this.fee = fee;
    }

    //constructor for error response
    public DeliveryFeeResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Double getFee() {
        return fee;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
