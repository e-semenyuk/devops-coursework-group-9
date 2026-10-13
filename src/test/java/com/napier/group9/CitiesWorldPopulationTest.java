package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class CitiesWorldPopulationTest {

    @Test
    void returnsAllCitiesOrderedByPopulationDescending() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var result = CityCommandResult.run(database.repository(), "PB-07");

            assertEquals(0, result.status());
            assertEquals("", result.errors());
            assertEquals("""
                Name | Country | District | Population
                Toronto | Canada | Ontario | 99,000,000
                London | United Kingdom | England | 3,000,000
                Paris | France | Île-de-France | 2,000,000
                Glasgow | United Kingdom | Scotland | 600,000
                Dublin | Ireland | Leinster | 500,000
                Edinburgh | United Kingdom | Scotland | 400,000
                Aberdeen | United Kingdom | Scotland | 200,000
                Dundee | United Kingdom | Scotland | 200,000
                """, result.output());
        }
    }

    @Test
    void supportsDescriptiveCommandAlias() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var pbResult = CityCommandResult.run(database.repository(), "PB-07");
            var namedResult = CityCommandResult.run(
                    database.repository(), "--cities-world-population");

            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void reportsWhenNoCitiesAreFound() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            database.execute("DELETE FROM city");

            var result = CityCommandResult.run(database.repository(), "PB-07");

            assertEquals(0, result.status());
            assertEquals("No cities found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void handlesDatabaseFailureWithoutExposingCredentials() {
        var broken = new CityPopulationRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = CityCommandResult.run(broken, "PB-07");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
        assertTrue(result.errors().contains("Could not generate city report."));
    }
}