package com.fujitsu.delivery.dto;

import java.math.BigDecimal;

public class DeliveryFeeResponse {

    private BigDecimal fee;
    private String errorMessage;

    //constructor for successful response
    public DeliveryFeeResponse (BigDecimal fee) {
        this.fee = fee;
    }

    //constructor for error response
    public DeliveryFeeResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
