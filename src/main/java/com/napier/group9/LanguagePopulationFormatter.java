package com.napier.group9;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

/** Formats the PB-32 five-language population report. */
public final class LanguagePopulationFormatter {
    private LanguagePopulationFormatter() {
    }

    public static void print(List<LanguagePopulation> languages, PrintStream output) {
        if (languages.isEmpty()) {
            output.println("No language population data found.");
            return;
        }

        output.println("Language | Speakers | World population %");

        for (LanguagePopulation language : languages) {
            output.printf(Locale.ROOT, "%s | %,d | %s%n",
                    language.language(),
                    language.speakers(),
                    language.worldPercentage());
        }
    }
}