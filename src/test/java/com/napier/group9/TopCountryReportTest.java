package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TopCountryReportTest {
    @Test
    void reportsTopCountriesInPopulationOrderAndLimitsAtDatabase() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            CountryCommandResult result = CountryCommandResult.run(database.repository(), "PB-04", "3");
            assertEquals(0, result.status());
            assertEquals("Code | Name | Continent | Region | Population | Capital\n"
                    + "ZZZ | Zulu | Europe | Europe | 10,000,000 | N/A\n"
                + "AAA | Alpha | Asia | East Asia | 5,000,000 | Alpha City\n"
                + "BBB | Bravo | Asia | East Asia | 5,000,000 | Bravo City\n", result.output());
            assertEquals("", result.errors());
            assertEquals(5, database.repository().findTopByPopulation(Integer.MAX_VALUE).size());
        }
    }

    @Test
    void shortCommandMatchesDescriptiveCommand() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            assertTrue(CountryReportCommand.supports("PB-04"));
            assertTrue(CountryReportCommand.supports("--top-countries-world"));
            assertEquals(CountryCommandResult.run(database.repository(), "PB-04", "3"),
                    CountryCommandResult.run(database.repository(), "--top-countries-world", "3"));
        }
    }

    @Test
    void returnsAllAvailableCountriesWhenNExceedsTableSize() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            CountryCommandResult result = CountryCommandResult.run(database.repository(), "PB-04", "10");
            assertEquals(0, result.status());
            assertEquals(5, result.output().lines().count() - 1);
            assertEquals("", result.errors());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "two", "1.5", "2147483648", "", " "})
    void rejectsInvalidNBeforeConnecting(String value) {
        CountryPopulationRepository noConnection = new CountryPopulationRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });
        CountryCommandResult result = CountryCommandResult.run(noConnection, "PB-04", value);
        assertEquals(2, result.status());
        assertEquals("", result.output());
        assertTrue(result.errors().contains("positive integer"));
    }

    @Test
    void rejectsMissingExtraAndNonPositiveRepositoryLimits() {
        CountryPopulationRepository noConnection = new CountryPopulationRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });
        assertEquals(2, CountryCommandResult.run(noConnection, "PB-04").status());
        assertEquals(2, CountryCommandResult.run(noConnection, "PB-04", "2", "extra").status());
        assertThrows(IllegalArgumentException.class, () -> noConnection.findTopByPopulation(0));
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CountryPopulationRepository broken = new CountryPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });
        CountryCommandResult result = CountryCommandResult.run(broken, "PB-04", "2");
        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertEquals("Could not generate country report. Check the database connection and world data.\n",
                result.errors());
    }
}