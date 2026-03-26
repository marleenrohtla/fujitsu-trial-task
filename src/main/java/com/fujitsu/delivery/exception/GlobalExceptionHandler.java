package com.fujitsu.delivery.exception;

import com.fujitsu.delivery.dto.DeliveryFeeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//handles exceptions globally for all controllers
//instead of handling errors in each controller separately, this class catches them all in one one place
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * handles validation errors from @Valid annotation
     * for example: when city or vehicleType is blank
     * @param ex - the validation exception containing field errors
     * @return 400 Bad Request with the first validation error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DeliveryFeeResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                // Get the error message defined in @NotBlank annotation
                .map(error -> error.getDefaultMessage())
                .orElse("Validation error");

        // Return 400 Bad Request with the error message
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DeliveryFeeResponse(message));
    }
    /**
     * handles all other runtime exceptions
     * for example: unknown city, forbidden vehicle type, no weather data found
     * @param ex - the runtime exception
     * @return 400 Bad Request with the error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DeliveryFeeResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DeliveryFeeResponse(ex.getMessage()));
    }
}