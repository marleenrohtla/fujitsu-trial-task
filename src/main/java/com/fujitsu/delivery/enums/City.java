package com.fujitsu.delivery.enums;

//enum representing the available cities for delivery
public enum City {
        TALLINN("Tallinn", "Tallinn-Harku"),
        TARTU("Tartu", "Tartu-Tõravere"),
        PÄRNU("Pärnu", "Pärnu");

        private final String displayName;
        private final String stationName;

        City(String displayName, String stationName) {
            this.displayName = displayName;
            this.stationName = stationName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getStationName() {
            return stationName;
        }

        public static City from(String value) {
            for (City city : values()) {
                if (city.displayName.equalsIgnoreCase(value)) {
                    return city;
                }
            }
            throw new RuntimeException("Unknown city: " + value);
        }
    }

