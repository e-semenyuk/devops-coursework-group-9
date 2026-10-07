package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class SelectedCityPopulationTest {

    @Test
    void returnsPopulationForSelectedCity() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = database.repository().findByCity("Belize City").orElseThrow();

            assertEquals("Belize City", result.name());
            assertEquals(50_000L, result.population());
        }
    }

    @Test
    void supportsCityNamesContainingSpaces() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = database.repository().findByCity("Orange Walk Town").orElseThrow();

            assertEquals("Orange Walk Town", result.name());
            assertEquals(15_000L, result.population());
        }
    }

    @Test
    void unknownCityReturnsNoPopulationData() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = PopulationTotalCommandResult.run(
                    database.repository(), "PB-31", "Unknown City");

            assertEquals(0, result.status());
            assertEquals("No population data found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void formatsCityPopulationAndAcceptsBothCommands() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var pbResult = PopulationTotalCommandResult.run(
                    database.repository(), "PB-31", "Belize City");
            var namedResult = PopulationTotalCommandResult.run(
                    database.repository(), "--population-city", "Belize City");

            assertEquals(0, pbResult.status());
            assertEquals("", pbResult.errors());
            assertTrue(pbResult.output().contains("Name | Population"));
            assertTrue(pbResult.output().contains("Belize City | 50,000"));
            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void rejectsMissingOrBlankCityBeforeConnecting() {
        var unused = new PopulationTotalRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });

        var missing = PopulationTotalCommandResult.run(unused, "PB-31");
        var blank = PopulationTotalCommandResult.run(unused, "PB-31", "   ");

        assertEquals(2, missing.status());
        assertEquals(2, blank.status());
        assertTrue(blank.errors().contains("City must not be blank."));
    }

    @Test
    void databaseErrorsDoNotLeakCredentials() {
        var broken = new PopulationTotalRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = PopulationTotalCommandResult.run(broken, "PB-31", "Belize City");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
    }
}
