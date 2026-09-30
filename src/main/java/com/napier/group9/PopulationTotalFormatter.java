package com.napier.group9;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Locale;

/** Formats population total reports. */
public final class PopulationTotalFormatter {
    private PopulationTotalFormatter() {
    }

    public static void print(Optional<PopulationTotal> total, PrintStream output) {
        if (total.isEmpty()) {
            output.println("No population data found.");
            return;
        }

        PopulationTotal result = total.get();
        output.println("Name | Population");
        output.printf(Locale.US, "%s | %,d%n", result.name(), result.population());
    }
}