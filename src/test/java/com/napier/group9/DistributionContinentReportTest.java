package com.napier.group9;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

class DistributionContinentReportTest {
    @Test
    void countsEachCountryOnceIncludesCountriesWithoutCitiesAndUsesLongTotals() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            List<PopulationDistribution> groups = database.repository().findByContinent();
            assertEquals(List.of("Antarctica", "Europe", "North America"),
                    groups.stream().map(PopulationDistribution::name).toList());
            PopulationDistribution europe = groups.get(1);
            assertEquals(5_000_000_025L, europe.totalPopulation());
            assertEquals(2_500_000_000L, europe.cityPopulation());
            assertEquals(2_500_000_025L, europe.nonCityPopulation());
            assertEquals(5_000_000_175L, groups.stream().mapToLong(PopulationDistribution::totalPopulation).sum());
            assertEquals(2_500_000_100L, groups.stream().mapToLong(PopulationDistribution::cityPopulation).sum());
        }
    }

    @Test
    void formatsAllColumnsAndZeroDenominatorsAndSupportsBothCommands() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            DistributionCommandResult result = DistributionCommandResult.run(database.repository(), "PB-23");
            assertEquals(0, result.status());
            assertEquals("", result.errors());
            assertEquals("Name | Total population | City population | City % | Non-city population | Non-city %\n"
                    + "Antarctica | 0 | 0 | N/A | 0 | N/A\n"
                    + "Europe | 5,000,000,025 | 2,500,000,000 | 50.00% | 2,500,000,025 | 50.00%\n"
                    + "North America | 150 | 100 | 66.67% | 50 | 33.33%\n", result.output());
            assertEquals(result, DistributionCommandResult.run(database.repository(), "--population-by-continent"));
        }
    }

    @Test
    void noCitiesMeansEveryoneOutsideCitiesAndNoCountriesMeansEmptyReport() throws Exception {
        try (DistributionTestDatabase database = new DistributionTestDatabase()) {
            database.execute("DELETE FROM city");
            for (PopulationDistribution group : database.repository().findByContinent()) {
                assertEquals(0, group.cityPopulation());
                assertEquals(group.totalPopulation(), group.nonCityPopulation());
            }
            database.execute("DELETE FROM country");
            assertEquals("No population data found.\n",
                    DistributionCommandResult.run(database.repository(), "PB-23").output());
        }
    }

    @Test
    void rejectsUnexpectedArgumentsBeforeConnecting() {
        PopulationDistributionRepository unused = new PopulationDistributionRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });
        assertEquals(2, DistributionCommandResult.run(unused).status());
        assertEquals(2, DistributionCommandResult.run(unused, "PB-23", "extra").status());
        assertEquals(2, DistributionCommandResult.run(unused, "PB-unknown").status());
    }

    @Test
    void databaseFailureIsSafeAndDistinctFromEmptyResults() {
        PopulationDistributionRepository broken = new PopulationDistributionRepository(() -> {
            throw new SQLException("password=private jdbc:mysql://private-host");
        });
        DistributionCommandResult result = DistributionCommandResult.run(broken, "PB-23");
        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertEquals("Could not generate population distribution. Check the database connection and world data.\n",
                result.errors());
    }
}

