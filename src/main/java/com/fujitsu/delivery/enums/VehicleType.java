package com.fujitsu.delivery.enums;

//enum representing the available vehicle types for delivery
public enum VehicleType {
    CAR("Car"),
    SCOOTER("Scooter"),
    BIKE("Bike");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static VehicleType from(String value) {
        for (VehicleType type : values()) {
            if (type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new RuntimeException("Unknown vehicle type: " + value);
    }
}

