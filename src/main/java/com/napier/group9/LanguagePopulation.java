package com.napier.group9;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Population information for one of the five major languages in PB-32. */
public record LanguagePopulation(String language, long speakers, long worldPopulation) {

    public String worldPercentage() {
        if (worldPopulation == 0) {
            return "N/A";
        }

        return BigDecimal.valueOf(speakers)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(worldPopulation), 2, RoundingMode.HALF_UP)
                .toPlainString() + "%";
    }
}