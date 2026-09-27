package com.napier.group9;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TopCityDistrictReportTest {
    @Test
    void filtersBeforeRankingAndLimiting() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            CityCommandResult result = CityCommandResult.run(database.repository(),
                    "--top-cities-district", " Scotland ", " 2 ");
            assertEquals(0, result.status());
            assertEquals("Name | Country | District | Population\n"
                    + "Glasgow | United Kingdom | Scotland | 600,000\nEdinburgh | United Kingdom | Scotland | 400,000\n", result.output());
            assertEquals("", result.errors());
            var cities = database.repository().findTopByDistrict("Scotland", Integer.MAX_VALUE);
            assertEquals(4, cities.size());
            assertEquals("Aberdeen", cities.get(cities.size() - 2).name());
            assertEquals("Dundee", cities.get(cities.size() - 1).name());
        }
    }

    @Test
    void shortCommandMatchesDescriptiveCommand() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            assertTrue(CityReportCommand.supports("PB-16"));
            assertEquals(CityCommandResult.run(database.repository(), "--top-cities-district", "Scotland", "2"),
                    CityCommandResult.run(database.repository(), "PB-16", "Scotland", "2"));
        }
    }

    @Test
    void unknownAndSqlLikeAreasAreLiteralAndEmpty() throws Exception {
        try (CityTestDatabase database = new CityTestDatabase()) {
            for (String area : new String[]{"Unknown", "Scotland' OR '1'='1", "O'Brien"}) {
                CityCommandResult result = CityCommandResult.run(database.repository(),
                        "--top-cities-district", area, "5");
                assertEquals(0, result.status());
                assertEquals("No cities found.\n", result.output());
            }
            database.execute("DELETE FROM city");
            assertEquals("No cities found.\n", CityCommandResult.run(database.repository(),
                    "--top-cities-district", "Scotland", "5").output());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "two", "1.5", "2147483648", "", " "})
    void rejectsInvalidNBeforeConnecting(String value) {
        CityCommandResult result = CityCommandResult.run(noConnection(),
                "--top-cities-district", "Scotland", value);
        assertEquals(2, result.status());
        assertEquals("", result.output());
        assertTrue(result.errors().contains("positive integer"));
    }

    @Test
    void rejectsBlankAreasMissingAndExtraArgumentsBeforeConnecting() {
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-district").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-district", "Scotland").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-district", "Scotland", "2", "extra").status());
        assertEquals(2, CityCommandResult.run(noConnection(), "--top-cities-district", " ", "2").status());
        assertThrows(IllegalArgumentException.class, () -> noConnection().findTopByDistrict(null, 2));
    }

    @Test
    void databaseErrorsDoNotExposeCredentials() {
        CityPopulationRepository broken = new CityPopulationRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });
        CityCommandResult result = CityCommandResult.run(broken, "--top-cities-district", "Scotland", "2");
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
