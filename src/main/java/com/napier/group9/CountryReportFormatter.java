package com.napier.group9;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

public final class CountryReportFormatter {
    private CountryReportFormatter() {
    }

    public static void print(List<CountryPopulation> countries, PrintStream output) {
        if (countries.isEmpty()) {
            output.println("No countries found.");
            return;
        }
        output.println("Code | Name | Continent | Region | Population | Capital");
        for (CountryPopulation country : countries) {
            String capital = country.capital() == null || country.capital().isBlank()
                    ? "N/A" : country.capital().trim();
            output.printf(Locale.ROOT, "%s | %s | %s | %s | %,d | %s%n",
                    country.code(), country.name(), country.continent(), country.region(),
                    country.population(), capital);
        }
    }
}
