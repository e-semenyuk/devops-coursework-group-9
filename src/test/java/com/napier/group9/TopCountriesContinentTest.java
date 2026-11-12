package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TopCountriesContinentTest {

    @Test
    void filtersByContinentOrdersByPopulationAndLimitsResults() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            CountryCommandResult result =
                    CountryCommandResult.run(database.repository(), "PB-05", "Asia", "2");

            assertEquals(0, result.status());
            assertEquals(
                    "Code | Name | Continent | Region | Population | Capital\n"
                            + "AAA | Alpha | Asia | East Asia | 5,000,000 | Alpha City\n"
                            + "BBB | Bravo | Asia | East Asia | 5,000,000 | Bravo City\n",
                    result.output());
            assertEquals("", result.errors());
            assertEquals(2, database.repository().findTopByContinent("Asia", 2).size());
        }
    }

    @Test
    void shortCommandMatchesDescriptiveCommand() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            assertTrue(CountryReportCommand.supports("PB-05"));
            assertTrue(CountryReportCommand.supports("--top-countries-continent"));

            assertEquals(
                    CountryCommandResult.run(database.repository(), "PB-05", "Asia", "2"),
                    CountryCommandResult.run(
                            database.repository(), "--top-countries-continent", "Asia", "2"));
        }
    }

    @Test
    void returnsAllAvailableCountriesWhenNExceedsContinentSize() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            int available = database.repository()
                    .findByContinent("Asia").size();

            CountryCommandResult result =
                    CountryCommandResult.run(database.repository(), "PB-05", "Asia", "100");

            assertEquals(0, result.status());
            assertEquals(available, result.output().lines().count() - 1);
            assertEquals("", result.errors());
        }
    }

    @Test
    void handlesNoMatchingCountries() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            CountryCommandResult result =
                    CountryCommandResult.run(database.repository(), "PB-05", "Antarctica", "3");

            assertEquals(0, result.status());
            assertTrue(result.output().contains("No countries found."));
            assertEquals("", result.errors());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "two", "1.5", "2147483648", "", " "})
    void rejectsInvalidNBeforeConnecting(String value) {
        CountryPopulationRepository noConnection = new CountryPopulationRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });

        CountryCommandResult result =
                CountryCommandResult.run(noConnection, "PB-05", "Asia", value);

        assertEquals(2, result.status());
        assertEquals("", result.output());
        assertTrue(result.errors().contains("positive integer"));
    }

    @Test
    void rejectsMissingExtraAndInvalidRepositoryArguments() {
        CountryPopulationRepository noConnection = new CountryPopulationRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });

        assertEquals(2, CountryCommandResult.run(noConnection, "PB-05").status());
        assertEquals(2, CountryCommandResult.run(
                noConnection, "PB-05", "Asia").status());
        assertEquals(2, CountryCommandResult.run(
                noConnection, "PB-05", "Asia", "2", "extra").status());

        assertThrows(IllegalArgumentException.class,
                () -> noConnection.findTopByContinent("Asia", 0));
        assertThrows(IllegalArgumentException.class,
                () -> noConnection.findTopByContinent(" ", 2));
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CountryPopulationRepository broken = new CountryPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });

        CountryCommandResult result =
                CountryCommandResult.run(broken, "PB-05", "Asia", "2");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertEquals(
                "Could not generate country report. Check the database connection and world data.\n",
                result.errors());
    }
}