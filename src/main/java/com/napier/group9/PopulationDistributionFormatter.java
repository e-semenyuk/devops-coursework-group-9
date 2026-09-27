package com.napier.group9;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

/** Displays totals and percentages without changing inconsistent source data. */
public final class PopulationDistributionFormatter {
    private PopulationDistributionFormatter() {
    }

    public static void print(List<PopulationDistribution> groups, PrintStream output) {
        if (groups.isEmpty()) {
            output.println("No population data found.");
            return;
        }
        output.println("Name | Total population | City population | City % | Non-city population | Non-city %");
        for (PopulationDistribution group : groups) {
            output.printf(Locale.ROOT, "%s | %,d | %,d | %s | %,d | %s%n",
                    group.name(), group.totalPopulation(), group.cityPopulation(), group.cityPercentage(),
                    group.nonCityPopulation(), group.nonCityPercentage());
        }
        for (PopulationDistribution group : groups) {
            if (group.cityPopulation() > group.totalPopulation()) {
                output.printf("Warning: %s has city population greater than its country total; "
                        + "derived figures reflect inconsistent source data.%n", group.name());
            }
        }
    }
}

