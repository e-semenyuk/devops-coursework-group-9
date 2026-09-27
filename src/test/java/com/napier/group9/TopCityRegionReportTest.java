package com.napier.group9;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TopCityRegionReportTest {
    @Test
    void filtersBeforeRankingAndLimiting() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            CityCommandResult result = CityCommandResult.run(database.repository(),
                    "--top-cities-region", " British Islands ", " 2 ");
            assertEquals(0, result.status());
            assertEquals("Name | Country | District | Population\n"
                    + "London | United Kingdom | England | 3,000,000\nGlasgow | United Kingdom | Scotland | 600,000\n", result.output());
            assertEquals("", result.errors());
            var cities = database.repository().findTopByRegion("British Islands", Integer.MAX_VALUE);
            assertEquals(6, cities.size());
            assertEquals("Aberdeen", cities.get(cities.size() - 2).name());
            assertEquals("Dundee", cities.get(cities.size() - 1).name());
        }
    }

    @Test
    void shortCommandMatchesDescriptiveCommand() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            assertTrue(CityReportCommand.supports("PB-14"));
            assertEquals(CityCommandResult.run(database.repository(), "--top-cities-region", "British Islands", "2"),
                    CityCommandResult.run(database.repository(), "PB-14", "British Islands", "2"));
        }
    }

    @Test
    void unknownAndSqlLikeAreasAreLiteralAndEmpty() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            for (String area : new String[]{"Unknown", "British Islands' OR '1'='1", "O'Brien"}) {
                CityCommandResult result = CityCommandResult.run(database.repository(),
                        "--top-cities-region", area, "5");
                assertEquals(0, result.status());
                assertEquals("No cities found.\n", result.output());
            }
            database.execute("DELETE FROM city");
            assertEquals("No cities found.\n", CityCommandResult.run(database.repository(),
                    "--top-cities-region", "British Islands", "5").output());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "two", "1.5", "2147483648", "", " "})
    void rejectsInvalidNBeforeConnecting(String value) {
        CityCommandResult result = CityCommandResult.run(noConnection(),
                "--top-cities-region", "British Islands", value);
        assertEquals(2, result.status());
        assertEquals("", result.output());
        assertTrue(result.errors().contains("positive integer"));
    }

    @Test
    void rejectsBlankAreasMissingAndExtraArgumentsBeforeConnecting() {
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-region").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-region", "British Islands").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-region", "British Islands", "2", "extra").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-region", " ", "2").status());
        assertThrows(IllegalArgumentException.class, () -> noConnection().findTopByRegion(null, 2));
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CityPopulationRepository broken = new CityPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });
        CityCommandResult result = CityCommandResult.run(broken, "--top-cities-region", "British Islands", "2");
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

