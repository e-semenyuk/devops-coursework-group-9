package com.napier.group9;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class SelectedDistrictPopulationTest {

    @Test
    void returnsPopulationForSelectedDistrict() throws Exception {
        try (PopulationTotalTestDatabase database =
                     new PopulationTotalTestDatabase()) {

            var result = database.repository()
                    .findByDistrict("Belize")
                    .orElseThrow();

            assertEquals("Belize", result.name());
            assertEquals(70_000L, result.population());
        }
    }

    @Test
    void supportsDistrictNamesContainingSpaces() throws Exception {
        try (PopulationTotalTestDatabase database =
                     new PopulationTotalTestDatabase()) {

            var result = database.repository()
                    .findByDistrict("Orange Walk")
                    .orElseThrow();

            assertEquals("Orange Walk", result.name());
            assertEquals(20_000L, result.population());
        }
    }

    @Test
    void unknownDistrictReturnsNoPopulationData() throws Exception {
        try (PopulationTotalTestDatabase database =
                     new PopulationTotalTestDatabase()) {

            var result = PopulationTotalCommandResult.run(
                    database.repository(), "PB-30", "Unknown District");

            assertEquals(0, result.status());
            assertEquals("No population data found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void formatsDistrictPopulationAndAcceptsBothCommands() throws Exception {
        try (PopulationTotalTestDatabase database =
                     new PopulationTotalTestDatabase()) {

            var pbResult = PopulationTotalCommandResult.run(
                    database.repository(), "PB-30", "Belize");

            var namedResult = PopulationTotalCommandResult.run(
                    database.repository(), "--population-district", "Belize");

            assertEquals(0, pbResult.status());
            assertEquals("", pbResult.errors());
            assertTrue(pbResult.output().contains("Name | Population"));
            assertTrue(pbResult.output().contains("Belize | 70,000"));
            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void rejectsMissingDistrictBeforeConnecting() {
        var unused = new PopulationTotalRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });

        var result = PopulationTotalCommandResult.run(
                unused, "PB-30");

        assertEquals(2, result.status());
    }

    @Test
    void rejectsBlankDistrict() {
        var unused = new PopulationTotalRepository(() -> {
            throw new AssertionError("Blank district must not connect");
        });

        var result = PopulationTotalCommandResult.run(
                unused, "PB-30", "   ");

        assertEquals(2, result.status());
        assertTrue(result.errors().contains("District must not be blank."));
    }

    @Test
    void databaseErrorsDoNotLeakCredentials() {
        var broken = new PopulationTotalRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = PopulationTotalCommandResult.run(
                broken, "PB-30", "Belize");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
    }
}