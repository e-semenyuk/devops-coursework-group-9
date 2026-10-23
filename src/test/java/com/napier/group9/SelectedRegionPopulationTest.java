package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class SelectedRegionPopulationTest {

    @Test
    void returnsPopulationForSelectedRegion() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = database.repository().findByRegion("Western Europe").orElseThrow();

            assertEquals("Western Europe", result.name());
            assertEquals(3_000_000L, result.population());
        }
    }

    @Test
    void unknownRegionReturnsNoPopulationData() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = PopulationTotalCommandResult.run(
                    database.repository(), "PB-28", "Unknown");

            assertEquals(0, result.status());
            assertEquals("No population data found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void formatsRegionPopulationAndAcceptsBothCommands() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var pbResult = PopulationTotalCommandResult.run(
                    database.repository(), "PB-28", "Western Europe");
            var namedResult = PopulationTotalCommandResult.run(
                    database.repository(), "--population-region", "Western Europe");

            assertEquals(0, pbResult.status());
            assertEquals("", pbResult.errors());
            assertTrue(pbResult.output().contains("Name | Population"));
            assertTrue(pbResult.output().contains("Western Europe | 3,000,000"));
            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void rejectsMissingOrBlankRegionBeforeConnecting() {
        var unused = new PopulationTotalRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });

        var missing = PopulationTotalCommandResult.run(unused, "PB-28");
        var blank = PopulationTotalCommandResult.run(unused, "PB-28", "   ");

        assertEquals(2, missing.status());
        assertEquals(2, blank.status());
        assertTrue(blank.errors().contains("Region must not be blank."));
    }

    @Test
    void databaseErrorsDoNotLeakCredentials() {
        var broken = new PopulationTotalRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = PopulationTotalCommandResult.run(
                broken, "PB-28", "Western Europe");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
    }
}
