package com.napier.group9;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Country totals and recorded city populations for one geographic group. */
public record PopulationDistribution(String name, long totalPopulation, long cityPopulation) {
    public long nonCityPopulation() {
        return totalPopulation - cityPopulation;
    }

    public String cityPercentage() {
        return percentage(cityPopulation);
    }

    public String nonCityPercentage() {
        return percentage(nonCityPopulation());
    }

    private String percentage(long population) {
        if (totalPopulation == 0) {
            return "N/A";
        }
        return BigDecimal.valueOf(population).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalPopulation), 2, RoundingMode.HALF_UP)
                .toPlainString() + "%";
    }
}

