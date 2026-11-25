package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TopCountryRegionReportTest {
    @Test
    void filtersBeforeLimitingAndSortsByPopulationThenName() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            database.execute("INSERT INTO country VALUES "
                    + "('HIG', 'High East', 'Asia', 'East Asia', 9000000, NULL), "
                    + "('OUT', 'Outside Region', 'Europe', 'Europe', 99000000, NULL)");

            List<CountryPopulation> countries = database.repository()
                    .findTopByRegion(" East Asia ", 2);
            assertEquals(List.of(
                    new CountryPopulation("HIG", "High East", "Asia", "East Asia", 9000000, null),
                    new CountryPopulation("AAA", "Alpha", "Asia", "East Asia", 5000000, "Alpha City")),
                    countries);

            CountryCommandResult result = CountryCommandResult.run(
                    database.repository(), "PB-06", "East Asia", "2");
            assertEquals(0, result.status());
            assertEquals("Code | Name | Continent | Region | Population | Capital\n"
                    + "HIG | High East | Asia | East Asia | 9,000,000 | N/A\n"
                    + "AAA | Alpha | Asia | East Asia | 5,000,000 | Alpha City\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void shortCommandMatchesDescriptiveAliasAndReturnsFewerThanNMatches() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            assertTrue(CountryReportCommand.supports("PB-06"));
            assertTrue(CountryReportCommand.supports("--top-countries-region"));

            CountryCommandResult shortCommand = CountryCommandResult.run(
                    database.repository(), "PB-06", "East Asia", "10");
            CountryCommandResult alias = CountryCommandResult.run(
                    database.repository(), "--top-countries-region", "East Asia", "10");
            assertEquals(shortCommand, alias);
            assertEquals(2, shortCommand.output().lines().count() - 1);
        }
    }

    @Test
    void unknownAndSqlLikeRegionsReturnNoCountries() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            for (String region : List.of("Unknown region", "East Asia' OR '1'='1")) {
                CountryCommandResult result = CountryCommandResult.run(
                        database.repository(), "--top-countries-region", region, "3");
                assertEquals(0, result.status());
                assertEquals("No countries found.\n", result.output());
                assertEquals("", result.errors());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "two", "1.5", "2147483648", "", " "})
    void rejectsInvalidNBeforeConnecting(String value) {
        CountryCommandResult result = CountryCommandResult.run(noConnection(),
                "PB-06", "East Asia", value);
        assertEquals(2, result.status());
        assertEquals("", result.output());
        assertTrue(result.errors().contains("positive integer"));
    }

    @Test
    void rejectsBlankRegionAndIncorrectArgumentCountsBeforeConnecting() {
        for (String[] args : List.of(new String[]{"PB-06"},
                new String[]{"PB-06", "East Asia"},
                new String[]{"PB-06", " ", "2"},
                new String[]{"PB-06", "East Asia", "2", "extra"})) {
            CountryCommandResult result = CountryCommandResult.run(noConnection(), args);
            assertEquals(2, result.status());
            assertEquals("", result.output());
            assertTrue(!result.errors().isBlank());
        }
        assertThrows(IllegalArgumentException.class,
                () -> noConnection().findTopByRegion("East Asia", 0));
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CountryPopulationRepository broken = new CountryPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });

        CountryCommandResult result = CountryCommandResult.run(
                broken, "PB-06", "East Asia", "2");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertEquals("Could not generate country report. Check the database connection and world data.\n",
                result.errors());
    }

    private static CountryPopulationRepository noConnection() {
        return new CountryPopulationRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });
    }
}