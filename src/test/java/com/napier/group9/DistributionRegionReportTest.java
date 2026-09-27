package com.napier.group9;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

class DistributionRegionReportTest {
    @Test
    void separatesRegionsWithinContinentAndCountsCountriesOnce() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            var groups = database.repository().findByRegion();
            assertEquals(List.of("Antarctica", "British Islands", "North America", "Western Europe"),
                    groups.stream().map(PopulationDistribution::name).toList());
            assertEquals(new PopulationDistribution("British Islands", 4_000_000_000L, 1_700_000_000L),
                    groups.get(1));
            assertEquals(new PopulationDistribution("Western Europe", 1_000_000_025L, 800_000_000L),
                    groups.get(3));
            assertEquals(5_000_000_175L, groups.stream().mapToLong(PopulationDistribution::totalPopulation).sum());
            assertEquals(2_500_000_100L, groups.stream().mapToLong(PopulationDistribution::cityPopulation).sum());
        }
    }

    @Test
    void formatsRegionPercentagesAndAcceptsBothCommands() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            var result = DistributionCommandResult.run(database.repository(), "PB-24");
            assertEquals(0, result.status());
            assertEquals("", result.errors());
            assertTrue(result.output().contains("British Islands | 4,000,000,000 | 1,700,000,000 | 42.50% | 2,300,000,000 | 57.50%"));
            assertTrue(result.output().contains("Antarctica | 0 | 0 | N/A | 0 | N/A"));
            assertEquals(result, DistributionCommandResult.run(database.repository(), "--population-by-region"));
        }
    }

    @Test
    void retainsRegionsWithNoCitiesAndHandlesEmptyData() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            database.execute("DELETE FROM city");
            assertEquals(4, database.repository().findByRegion().size());
            assertTrue(database.repository().findByRegion().stream().allMatch(row -> row.cityPopulation() == 0));
            database.execute("DELETE FROM country");
            assertEquals("No population data found.\n",
                    DistributionCommandResult.run(database.repository(), "PB-24").output());
        }
    }

    @Test
    void rejectsExtraArgumentsBeforeConnecting() {
        var unused = new PopulationDistributionRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });
        assertEquals(2, DistributionCommandResult.run(unused, "PB-24", "extra").status());
    }

    @Test
    void databaseFailureDoesNotPrintPartialReportOrConnectionDetails() {
        var broken = new PopulationDistributionRepository(() -> {
            throw new SQLException("password=private");
        });
        var result = DistributionCommandResult.run(broken, "PB-24");
        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
    }
}

