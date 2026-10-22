
package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class CitiesContinentPopulationTest {

    @Test
    void returnsOnlyCitiesInSelectedContinentOrderedByPopulation() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var result = CityCommandResult.run(database.repository(), "PB-08", "Europe");

            assertEquals(0, result.status());
            assertEquals("", result.errors());
            assertEquals("""
                    Name | Country | District | Population
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
            var pbResult = CityCommandResult.run(database.repository(), "PB-08", "Europe");
            var namedResult = CityCommandResult.run(
                    database.repository(), "--cities-continent-population", "Europe");

            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void reportsWhenNoCitiesAreFound() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var result = CityCommandResult.run(database.repository(), "PB-08", "Antarctica");

            assertEquals(0, result.status());
            assertEquals("No cities found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void rejectsMissingContinentArgument() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var result = CityCommandResult.run(database.repository(), "PB-08");

            assertEquals(2, result.status());
            assertTrue(result.errors().contains("PB-08"));
        }
    }

    @Test
    void rejectsBlankContinentArgument() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            var result = CityCommandResult.run(database.repository(), "PB-08", " ");

            assertEquals(2, result.status());
            assertTrue(result.errors().contains("Continent must not be blank."));
        }
    }

    @Test
    void handlesDatabaseFailureWithoutExposingCredentials() {
        var broken = new CityPopulationRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = CityCommandResult.run(broken, "PB-08", "Europe");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
        assertTrue(result.errors().contains("Could not generate city report."));
    }
}
