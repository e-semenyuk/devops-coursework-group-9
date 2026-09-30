package com.napier.group9;

import java.io.PrintStream;
import java.util.Optional;

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
        output.printf("%s | %,d%n", result.name(), result.population());
    }
}