package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TopCityWorldReportTest {
    @Test
    void reportsTopCitiesInPopulationOrderAndLimitsAtDatabase() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            CityCommandResult result = CityCommandResult.run(database.repository(),
                    "--top-cities-world", " 3 ");
            assertEquals(0, result.status());
            assertEquals("Name | Country | District | Population\n"
                    + "Toronto | Canada | Ontario | 99,000,000\n"
                    + "London | United Kingdom | England | 3,000,000\n"
                    + "Paris | France | Île-de-France | 2,000,000\n", result.output());
            assertEquals("", result.errors());
            assertEquals(8, database.repository().findTopByPopulation(Integer.MAX_VALUE).size());
        }
    }

    @Test
    void shortCommandMatchesDescriptiveCommand() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            assertTrue(CityReportCommand.supports("PB-12"));
            assertTrue(CityReportCommand.supports("--top-cities-world"));
            assertEquals(CityCommandResult.run(database.repository(), "PB-12", "2"),
                    CityCommandResult.run(database.repository(), "--top-cities-world", "2"));
        }
    }

    @Test
    void returnsAllAvailableCitiesWhenNExceedsTableSize() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            CityCommandResult result = CityCommandResult.run(database.repository(),
                    "--top-cities-world", "10");
            assertEquals(0, result.status());
            assertEquals(8, result.output().lines().count() - 1);
            assertEquals("", result.errors());
        }
    }

    @Test
    void emptyCityDataDisplaysNoCitiesFound() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            database.execute("DELETE FROM city");
            CityCommandResult result = CityCommandResult.run(database.repository(),
                    "--top-cities-world", "5");
            assertEquals(0, result.status());
            assertEquals("No cities found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "two", "1.5", "2147483648", "", " "})
    void rejectsInvalidNBeforeConnecting(String value) {
        CityCommandResult result = CityCommandResult.run(noConnection(),
                "--top-cities-world", value);
        assertEquals(2, result.status());
        assertEquals("", result.output());
        assertTrue(result.errors().contains("positive integer"));
    }

    @Test
    void rejectsMissingExtraAndNonPositiveRepositoryLimits() {
        assertEquals(2, CityCommandResult.run(noConnection(), "PB-12").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "PB-12", "2", "extra").status());
        assertThrows(IllegalArgumentException.class,
                () -> noConnection().findTopByPopulation(0));
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CityPopulationRepository broken = new CityPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });
        CityCommandResult result = CityCommandResult.run(broken,
                "--top-cities-world", "2");
        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertEquals("Could not generate city report. Check the database connection and world data.\n",
                result.errors());
    }

    private static CityPopulationRepository noConnection() {
        return new CityPopulationRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });
    }
}
