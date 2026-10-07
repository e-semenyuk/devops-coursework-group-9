package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class SelectedContinentPopulationTest {

    @Test
    void returnsPopulationForSelectedContinent() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = database.repository().findByContinent("Europe").orElseThrow();

            assertEquals("Europe", result.name());
            assertEquals(3_000_000L, result.population());
        }
    }

    @Test
    void unknownContinentReturnsNoPopulationData() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = PopulationTotalCommandResult.run(
                    database.repository(), "PB-27", "Unknown");

            assertEquals(0, result.status());
            assertEquals("No population data found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void formatsContinentPopulationAndAcceptsBothCommands() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var pbResult = PopulationTotalCommandResult.run(
                    database.repository(), "PB-27", "Europe");
            var namedResult = PopulationTotalCommandResult.run(
                    database.repository(), "--population-continent", "Europe");

            assertEquals(0, pbResult.status());
            assertEquals("", pbResult.errors());
            assertTrue(pbResult.output().contains("Name | Population"));
            assertTrue(pbResult.output().contains("Europe | 3,000,000"));
            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void rejectsMissingOrBlankContinentBeforeConnecting() {
        var unused = new PopulationTotalRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });

        var missing = PopulationTotalCommandResult.run(unused, "PB-27");
        var blank = PopulationTotalCommandResult.run(unused, "PB-27", "   ");

        assertEquals(2, missing.status());
        assertEquals(2, blank.status());
        assertTrue(blank.errors().contains("Continent must not be blank."));
    }

    @Test
    void databaseErrorsDoNotLeakCredentials() {
        var broken = new PopulationTotalRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = PopulationTotalCommandResult.run(
                broken, "PB-27", "Europe");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
    }
}
