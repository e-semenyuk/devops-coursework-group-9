package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

class CountryReportTest {
    @Test
    void listsEveryCountryByDescendingPopulationWithStableTiesAndCapitalNames() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            List<CountryPopulation> countries = database.repository().findAllByPopulation();
            assertEquals(List.of(
                    new CountryPopulation("ZZZ", "Zulu", "Europe", "Europe", 10000000, null),
                    new CountryPopulation("AAA", "Alpha", "Asia", "East Asia", 5000000, "Alpha City"),
                    new CountryPopulation("BBB", "Bravo", "Asia", "East Asia", 5000000, "Bravo City"),
                    new CountryPopulation("BAD", "Broken link", "Africa", "Central Africa", 1000, null),
                    new CountryPopulation("LOW", "Low", "Oceania", "Australia", 200, null)), countries);

            CountryCommandResult result = CountryCommandResult.run(database.repository(), "PB-01");
            assertEquals(0, result.status());
            assertEquals("Code | Name | Continent | Region | Population | Capital\n"
                    + "ZZZ | Zulu | Europe | Europe | 10,000,000 | N/A\n"
                    + "AAA | Alpha | Asia | East Asia | 5,000,000 | Alpha City\n"
                    + "BBB | Bravo | Asia | East Asia | 5,000,000 | Bravo City\n"
                    + "BAD | Broken link | Africa | Central Africa | 1,000 | N/A\n"
                    + "LOW | Low | Oceania | Australia | 200 | N/A\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void descriptiveAliasProducesSameReport() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            assertTrue(CountryReportCommand.supports("PB-01"));
            assertTrue(CountryReportCommand.supports("--countries-world"));
            assertEquals(CountryCommandResult.run(database.repository(), "PB-01"),
                    CountryCommandResult.run(database.repository(), "--countries-world"));
        }
    }

    @Test
    void emptyCountryTableHasClearOutput() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            database.execute("DELETE FROM country");
            CountryCommandResult result = CountryCommandResult.run(database.repository(), "PB-01");
            assertEquals(0, result.status());
            assertEquals("No countries found.\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void rejectsInvalidArgumentsBeforeConnecting() {
        CountryPopulationRepository noConnection = new CountryPopulationRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });
        assertEquals(2, CountryCommandResult.run(noConnection).status());
        assertEquals(2, CountryCommandResult.run(noConnection, "PB-01", "extra").status());
        assertEquals(2, CountryCommandResult.run(noConnection, "unknown").status());
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CountryPopulationRepository broken = new CountryPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });
        CountryCommandResult result = CountryCommandResult.run(broken, "PB-01");
        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertEquals("Could not generate country report. Check the database connection and world data.\n",
                result.errors());
    }
}
