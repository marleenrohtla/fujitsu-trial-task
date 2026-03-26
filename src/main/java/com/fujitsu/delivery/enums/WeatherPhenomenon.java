package com.fujitsu.delivery.enums;

import java.util.Arrays;
import java.util.Optional;

//enum representing weather phenomena that affect delivery fees
public enum WeatherPhenomenon {
    //forbidden phenomena - vehicle usage is not allowed
    GLAZE("glaze", Behavior.FORBIDDEN),
    HAIL("hail", Behavior.FORBIDDEN),
    THUNDER("thunder", Behavior.FORBIDDEN),

    //high fee phenomena - adds 1€ extra free
    SNOW("snow", Behavior.HIGH_FEE),
    SLEET("sleet", Behavior.HIGH_FEE),

    //low fee phenomena - adds 0.5€ extra fee
    RAIN("rain", Behavior.LOW_FEE);

    // enum representing how a weather phenomenon affects the delivery fee
    public enum Behavior {
        FORBIDDEN,
        HIGH_FEE,
        LOW_FEE
    }

    private final String keyword;
    private final Behavior behavior;

    //sets the keyword and behavior for each weather phenomenon
    WeatherPhenomenon(String keyword, Behavior behavior) {
        this.keyword = keyword;
        this.behavior = behavior;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public String getKeyword() {
        return keyword;
    }
    /**
     * finds a WeatherPhenomenon from a text string
     * for example: "Light snow shower" matches SNOW
     * @param text - weather phenomenon text from the XML
     * @return matching WeatherPhenomenon or empty if no match found
     */
    public static Optional<WeatherPhenomenon> fromText(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        String normalized = text.toLowerCase();

        // find the first phenomenon whose keyword is contained in the text
        // for example: "light snow shower" contains "snow" so it matches SNOW
        return Arrays.stream(values())
                .filter(phenomenon -> normalized.contains(phenomenon.keyword))
                .findFirst();
    }
}
