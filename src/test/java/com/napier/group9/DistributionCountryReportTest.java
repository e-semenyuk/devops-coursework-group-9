package com.napier.group9;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class DistributionCountryReportTest {
    @Test
    void returnsOneRowPerCountryIncludingCountriesWithoutCities() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            var groups = database.repository().findByCountry();
            assertEquals(7, groups.size());
            assertEquals(new PopulationDistribution("United Kingdom", 3_000_000_000L, 1_500_000_000L),
                    groups.stream().filter(row -> row.name().equals("United Kingdom")).findFirst().orElseThrow());
            assertEquals(new PopulationDistribution("No cities", 25, 0),
                    groups.stream().filter(row -> row.name().equals("No cities")).findFirst().orElseThrow());
            assertEquals(5_000_000_175L, groups.stream().mapToLong(PopulationDistribution::totalPopulation).sum());
            assertEquals(2_500_000_100L, groups.stream().mapToLong(PopulationDistribution::cityPopulation).sum());
            assertEquals("Canada", groups.get(0).name());
            assertEquals("Zero population", groups.get(groups.size() - 1).name());
        }
    }

    @Test
    void countriesWithSameNameRemainSeparate() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            database.execute("INSERT INTO country VALUES ('DUP', 'United Kingdom', 'Europe', 'British Islands', 123)");
            var sameNames = database.repository().findByCountry().stream()
                    .filter(row -> row.name().equals("United Kingdom")).toList();
            assertEquals(2, sameNames.size());
            assertEquals(123, sameNames.get(0).totalPopulation());
            assertEquals(3_000_000_000L, sameNames.get(1).totalPopulation());
        }
    }

    @Test
    void formatsPercentagesFlagsInconsistentDataAndAcceptsBothCommands() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            var result = DistributionCommandResult.run(database.repository(), "PB-25");
            assertEquals(0, result.status());
            assertEquals("", result.errors());
            assertTrue(result.output().contains("United Kingdom | 3,000,000,000 | 1,500,000,000 | 50.00% | 1,500,000,000 | 50.00%"));
            assertTrue(result.output().contains("Zero population | 0 | 0 | N/A | 0 | N/A"));
            assertTrue(result.output().contains("Inconsistent example | 50 | 75 | 150.00% | -25 | -50.00%"));
            assertTrue(result.output().contains("Warning: Inconsistent example"));
            assertEquals(result, DistributionCommandResult.run(database.repository(), "--population-by-country"));
        }
    }

    @Test
    void handlesNoCitiesAndEmptyCountries() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            database.execute("DELETE FROM city");
            assertEquals(7, database.repository().findByCountry().size());
            assertTrue(database.repository().findByCountry().stream().allMatch(row -> row.cityPopulation() == 0));
            database.execute("DELETE FROM country");
            assertEquals("No population data found.\n",
                    DistributionCommandResult.run(database.repository(), "PB-25").output());
        }
    }

    @Test
    void rejectsExtraArgumentsBeforeConnecting() {
        var unused = new PopulationDistributionRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });
        assertEquals(2, DistributionCommandResult.run(unused, "PB-25", "extra").status());
    }

    @Test
    void databaseErrorsDoNotLeakCredentials() {
        var broken = new PopulationDistributionRepository(() -> {
            throw new SQLException("password=private");
        });
        var result = DistributionCommandResult.run(broken, "PB-25");
        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
    }
}
