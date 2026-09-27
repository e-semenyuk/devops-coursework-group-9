package com.napier.group9;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TopCityContinentReportTest {
    @Test
    void filtersBeforeRankingAndLimiting() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            CityCommandResult result = CityCommandResult.run(database.repository(),
                    "--top-cities-continent", " Europe ", " 2 ");
            assertEquals(0, result.status());
            assertEquals("Name | Country | District | Population\n"
                    + "London | United Kingdom | England | 3,000,000\nParis | France | Île-de-France | 2,000,000\n", result.output());
            assertEquals("", result.errors());
            var cities = database.repository().findTopByContinent("Europe", Integer.MAX_VALUE);
            assertEquals(7, cities.size());
            assertEquals("Aberdeen", cities.get(cities.size() - 2).name());
            assertEquals("Dundee", cities.get(cities.size() - 1).name());
        }
    }

    @Test
    void shortCommandMatchesDescriptiveCommand() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            assertTrue(CityReportCommand.supports("PB-13"));
            assertEquals(CityCommandResult.run(database.repository(), "--top-cities-continent", "Europe", "2"),
                    CityCommandResult.run(database.repository(), "PB-13", "Europe", "2"));
        }
    }

    @Test
    void unknownAndSqlLikeAreasAreLiteralAndEmpty() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            for (String area : new String[]{"Unknown", "Europe' OR '1'='1", "O'Brien"}) {
                CityCommandResult result = CityCommandResult.run(database.repository(),
                        "--top-cities-continent", area, "5");
                assertEquals(0, result.status());
                assertEquals("No cities found.\n", result.output());
            }
            database.execute("DELETE FROM city");
            assertEquals("No cities found.\n", CityCommandResult.run(database.repository(),
                    "--top-cities-continent", "Europe", "5").output());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "two", "1.5", "2147483648", "", " "})
    void rejectsInvalidNBeforeConnecting(String value) {
        CityCommandResult result = CityCommandResult.run(noConnection(),
                "--top-cities-continent", "Europe", value);
        assertEquals(2, result.status());
        assertEquals("", result.output());
        assertTrue(result.errors().contains("positive integer"));
    }

    @Test
    void rejectsBlankAreasMissingAndExtraArgumentsBeforeConnecting() {
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-continent").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-continent", "Europe").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-continent", "Europe", "2", "extra").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-continent", " ", "2").status());
        assertThrows(IllegalArgumentException.class, () -> noConnection().findTopByContinent(null, 2));
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CityPopulationRepository broken = new CityPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });
        CityCommandResult result = CityCommandResult.run(broken, "--top-cities-continent", "Europe", "2");
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

