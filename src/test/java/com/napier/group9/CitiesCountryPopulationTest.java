package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class CitiesCountryPopulationTest {

    @Test
    void returnsOnlyCitiesInSelectedCountryOrderedByPopulation() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var result = CityCommandResult.run(
                    database.repository(), "PB-10", "United Kingdom");

            assertEquals(0, result.status());
            assertEquals("", result.errors());
            assertEquals("""
                    Name | Country | District | Population
                    London | United Kingdom | England | 3,000,000
                    Glasgow | United Kingdom | Scotland | 600,000
                    Edinburgh | United Kingdom | Scotland | 400,000
                    Aberdeen | United Kingdom | Scotland | 200,000
                    Dundee | United Kingdom | Scotland | 200,000
                    """, result.output());
        }
    }

    @Test
    void supportsDescriptiveCommandAlias() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var pbResult = CityCommandResult.run(
                    database.repository(), "PB-10", "United Kingdom");
            var namedResult = CityCommandResult.run(
                    database.repository(), "--cities-country-population", "United Kingdom");

            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void reportsWhenNoCitiesAreFound() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var result = CityCommandResult.run(
                    database.repository(), "PB-10", "Nonexistent Country");

            assertEquals(0, result.status());
            assertEquals("No cities found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void rejectsMissingCountryArgument() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var result = CityCommandResult.run(database.repository(), "PB-10");

            assertEquals(2, result.status());
            assertTrue(result.errors().contains("PB-10"));
        }
    }

    @Test
    void rejectsBlankCountryArgument() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var result = CityCommandResult.run(database.repository(), "PB-10", " ");

            assertEquals(2, result.status());
            assertTrue(result.errors().contains("Country must not be blank."));
        }
    }

    @Test
    void handlesDatabaseFailureWithoutExposingCredentials() {
        var broken = new CityPopulationRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = CityCommandResult.run(broken, "PB-10", "United Kingdom");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
        assertTrue(result.errors().contains("Could not generate city report."));
    }
}
