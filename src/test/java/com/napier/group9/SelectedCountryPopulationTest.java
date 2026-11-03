package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class SelectedCountryPopulationTest {

    @Test
    void returnsPopulationForSelectedCountry() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = database.repository().findByCountry("Country A").orElseThrow();

            assertEquals("Country A", result.name());
            assertEquals(1_000_000L, result.population());
        }
    }

    @Test
    void unknownCountryReturnsNoPopulationData() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = PopulationTotalCommandResult.run(
                    database.repository(), "PB-29", "Unknown");

            assertEquals(0, result.status());
            assertEquals("No population data found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void formatsCountryPopulationAndAcceptsBothCommands() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var pbResult = PopulationTotalCommandResult.run(
                    database.repository(), "PB-29", "Country A");
            var namedResult = PopulationTotalCommandResult.run(
                    database.repository(), "--population-country", "Country A");

            assertEquals(0, pbResult.status());
            assertEquals("", pbResult.errors());
            assertTrue(pbResult.output().contains("Name | Population"));
            assertTrue(pbResult.output().contains("Country A | 1,000,000"));
            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void rejectsMissingOrBlankCountryBeforeConnecting() {
        var unused = new PopulationTotalRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });

        var missing = PopulationTotalCommandResult.run(unused, "PB-29");
        var blank = PopulationTotalCommandResult.run(unused, "PB-29", "   ");

        assertEquals(2, missing.status());
        assertEquals(2, blank.status());
        assertTrue(blank.errors().contains("Country must not be blank."));
    }

    @Test
    void databaseErrorsDoNotLeakCredentials() {
        var broken = new PopulationTotalRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = PopulationTotalCommandResult.run(
                broken, "PB-29", "Country A");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
    }
}
