package com.napier.group9;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

public final class CapitalReportFormatter {
    private CapitalReportFormatter() {
    }

    public static void print(List<CapitalCity> capitals, PrintStream output) {
        if (capitals.isEmpty()) {
            output.println("No capital cities found.");
            return;
        }
        output.println("Name | Country | Population");
        for (CapitalCity capital : capitals) {
            output.printf(Locale.ROOT, "%s | %s | %,d%n",
                    capital.name(), capital.country(), capital.population());
        }
    }
}
