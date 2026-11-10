package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

/**
 * Verifies population reporting repositories against
 * the MySQL database used by GitHub Actions.
 */
class PopulationTotalMySqlIT {

    @Test
    void readsTheWorldPopulationFromMySql() throws SQLException {
        DatabaseConfig config = DatabaseConfig.fromEnvironment();
        PopulationTotalRepository repository =
                new PopulationTotalRepository(config);

        var result = repository.findWorldPopulation();

        assertTrue(result.isPresent());
        assertEquals("World", result.orElseThrow().name());
        assertEquals(68_000_000L, result.orElseThrow().population());
    }

    @Test
    void readsCityReportFromMySql() throws SQLException {
        DatabaseConfig config = DatabaseConfig.fromEnvironment();
        CityPopulationRepository repository =
                new CityPopulationRepository(config);

        var cities = repository.findAllByPopulation();

        assertEquals(1, cities.size());
        assertEquals("London", cities.get(0).name());
        assertEquals("United Kingdom", cities.get(0).country());
        assertEquals(9_000_000L, cities.get(0).population());
    }

    @Test
    void readsCountryReportFromMySql() throws SQLException {
        DatabaseConfig config = DatabaseConfig.fromEnvironment();
        CountryPopulationRepository repository =
                new CountryPopulationRepository(config);

        var countries = repository.findAllByPopulation();

        assertEquals(1, countries.size());
        assertEquals("GBR", countries.get(0).code());
        assertEquals("United Kingdom", countries.get(0).name());
        assertEquals(68_000_000L, countries.get(0).population());
        assertEquals("London", countries.get(0).capital());
    }

    @Test
    void returnsEmptyResultsForUnknownContinent() throws SQLException {
        DatabaseConfig config = DatabaseConfig.fromEnvironment();
        CityPopulationRepository repository =
                new CityPopulationRepository(config);

        var cities = repository.findByContinent("Antarctica");

        assertTrue(cities.isEmpty());
    }
}