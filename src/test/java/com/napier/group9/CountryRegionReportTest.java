package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

class CountryRegionReportTest {
    @Test
    void filtersByRegionAndSortsByDescendingPopulation() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            database.execute("INSERT INTO country VALUES "
                    + "('HIG', 'High East', 'Asia', 'East Asia', 9000000, NULL), "
                    + "('OTH', 'Other Region', 'Europe', 'Europe', 99000000, NULL)");

            List<CountryPopulation> countries = database.repository().findByRegion(" East Asia ");
            assertEquals(List.of(
                    new CountryPopulation("HIG", "High East", "Asia", "East Asia", 9000000, null),
                    new CountryPopulation("AAA", "Alpha", "Asia", "East Asia", 5000000, "Alpha City"),
                    new CountryPopulation("BBB", "Bravo", "Asia", "East Asia", 5000000, "Bravo City")),
                    countries);

            CountryCommandResult result = CountryCommandResult.run(
                    database.repository(), "PB-03", "East Asia");
            assertEquals(0, result.status());
            assertEquals("Code | Name | Continent | Region | Population | Capital\n"
                    + "HIG | High East | Asia | East Asia | 9,000,000 | N/A\n"
                    + "AAA | Alpha | Asia | East Asia | 5,000,000 | Alpha City\n"
                    + "BBB | Bravo | Asia | East Asia | 5,000,000 | Bravo City\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void shortCommandMatchesDescriptiveAlias() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            assertTrue(CountryReportCommand.supports("PB-03"));
            assertTrue(CountryReportCommand.supports("--countries-region"));
            assertEquals(CountryCommandResult.run(database.repository(), "PB-03", "East Asia"),
                    CountryCommandResult.run(database.repository(), "--countries-region", "East Asia"));
        }
    }

    @Test
    void unknownAndSqlLikeRegionsAreLiteralAndEmpty() throws Exception {
        try (CountryTestDatabase database = new CountryTestDatabase()) {
            for (String region : new String[]{"Unknown", "East Asia' OR '1'='1", "O'Brien"}) {
                CountryCommandResult result = CountryCommandResult.run(
                        database.repository(), "--countries-region", region);
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

        assertEquals(2, CountryCommandResult.run(noConnection, "PB-03").status());
        assertEquals(2, CountryCommandResult.run(noConnection, "--countries-region", "East Asia", "extra").status());
        assertEquals(2, CountryCommandResult.run(noConnection, "PB-03", " ").status());
        assertThrows(IllegalArgumentException.class, () -> noConnection.findByRegion(null));
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CountryPopulationRepository broken = new CountryPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });

        CountryCommandResult result = CountryCommandResult.run(broken, "PB-03", "East Asia");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertEquals("Could not generate country report. Check the database connection and world data.\n",
                result.errors());
    }
}