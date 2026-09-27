package com.napier.group9;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class DistributionFormatterTest {
    @Test
    void roundsFractionsToTwoDecimalPlaces() {
        PopulationDistribution third = new PopulationDistribution("Example", 3, 1);
        assertEquals("33.33%", third.cityPercentage());
        assertEquals("66.67%", third.nonCityPercentage());
        assertEquals("0.00%", new PopulationDistribution("Empty", 100, 0).cityPercentage());
        assertEquals("100.00%", new PopulationDistribution("All", 100, 100).cityPercentage());
    }

    @Test
    void retainsAndFlagsInconsistentSourceData() {
        PopulationDistribution inconsistent = new PopulationDistribution("Example", 50, 75);
        assertEquals(-25, inconsistent.nonCityPopulation());
        assertEquals("150.00%", inconsistent.cityPercentage());
        assertEquals("-50.00%", inconsistent.nonCityPercentage());
        String output = render(List.of(inconsistent));
        assertTrue(output.contains("Example | 50 | 75 | 150.00% | -25 | -50.00%"));
        assertTrue(output.contains("Warning: Example has city population greater than its country total"));
        PopulationDistribution zero = new PopulationDistribution("Zero", 0, 5);
        assertEquals("N/A", zero.cityPercentage());
        assertEquals("N/A", zero.nonCityPercentage());
    }

    @Test
    void outputIsIndependentOfMachineLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            assertTrue(render(List.of(new PopulationDistribution("Example", 2000, 500)))
                    .contains("Example | 2,000 | 500 | 25.00% | 1,500 | 75.00%"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    private static String render(List<PopulationDistribution> groups) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PopulationDistributionFormatter.print(groups, new PrintStream(output, true, StandardCharsets.UTF_8));
        return output.toString(StandardCharsets.UTF_8);
    }
}

