package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class CountryContinentReportTest {
    @Test
    void filtersByContinentAndSortsByDescendingPopulation() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            database.execute("INSERT INTO country VALUES ('HIG', 'High Asia', 'Asia', 'East Asia', 9000000, NULL)");

            CountryCommandResult result = CountryCommandResult.run(database.repository(), "PB-02", " Asia ");

            assertEquals(0, result.status());
            assertEquals("Code | Name | Continent | Region | Population | Capital\n"
                    + "HIG | High Asia | Asia | East Asia | 9,000,000 | N/A\n"
                    + "AAA | Alpha | Asia | East Asia | 5,000,000 | Alpha City\n"
                    + "BBB | Bravo | Asia | East Asia | 5,000,000 | Bravo City\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void shortCommandMatchesDescriptiveAlias() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            assertTrue(CountryReportCommand.supports("PB-02"));
            assertTrue(CountryReportCommand.supports("--countries-continent"));
            assertEquals(CountryCommandResult.run(database.repository(), "PB-02", "Asia"),
                    CountryCommandResult.run(database.repository(), "--countries-continent", "Asia"));
        }
    }

    @Test
    void unknownAndSqlLikeContinentsAreLiteralAndEmpty() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            for (String continent : new String[]{"Unknown", "Asia' OR '1'='1", "O'Brien"}) {
                CountryCommandResult result = CountryCommandResult.run(
                        database.repository(), "--countries-continent", continent);
                assertEquals(0, result.status());
                assertEquals("No countries found.\n", result.output());
                assertEquals("", result.errors());
            }
        }
    }

    @Test
    void rejectsBlankMissingAndExtraArgumentsBeforeConnecting() {
        CountryPopulationRepository noConnection = new CountryPopulationRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });

        assertEquals(2, CountryCommandResult.run(noConnection, "PB-02").status());
        assertEquals(2, CountryCommandResult.run(noConnection, "--countries-continent", "Asia", "extra").status());
        assertEquals(2, CountryCommandResult.run(noConnection, "PB-02", " ").status());
        assertThrows(IllegalArgumentException.class, () -> noConnection.findByContinent(null));
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CountryPopulationRepository broken = new CountryPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });

        CountryCommandResult result = CountryCommandResult.run(broken, "PB-02", "Asia");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertEquals("Could not generate country report. Check the database connection and world data.\n",
                result.errors());
    }
}