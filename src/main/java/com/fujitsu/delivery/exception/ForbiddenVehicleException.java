package com.fujitsu.delivery.exception;

/**
 * exception thrown when the selected vehicle type is forbidden
 * due to dangerous weather
 */

public class ForbiddenVehicleException extends RuntimeException{

    private static final String MESSAGE = "Usage of selected vehicle type is forbidden!";

    public ForbiddenVehicleException() {
        // super() calls the parent class (RuntimeException) constructor
        // and passes it the error message
        super(MESSAGE);
    }

    public String getMessage() {
        return MESSAGE;
    }
}
