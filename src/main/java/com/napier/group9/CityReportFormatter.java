package com.napier.group9;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

/** Renders city reports with all four required columns. */
public final class CityReportFormatter {
    private CityReportFormatter() {
    }

    public static void print(List<CityPopulation> cities, PrintStream output) {
        if (cities.isEmpty()) {
            output.println("No cities found.");
            return;
        }
        output.println("Name | Country | District | Population");
        for (CityPopulation city : cities) {
            output.printf(Locale.ROOT, "%s | %s | %s | %,d%n",
                    city.name(), city.country(), city.district(), city.population());
        }
    }
}
