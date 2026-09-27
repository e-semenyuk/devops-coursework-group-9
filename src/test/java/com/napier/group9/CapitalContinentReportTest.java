package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CapitalContinentReportTest {
    @Test
    void filtersByCountryContinentAndKeepsDescendingCapitalPopulation() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            assertEquals(List.of(new CapitalCity("London", "United Kingdom", 3000000),
                    new CapitalCity("Dublin", "Ireland", 2000000)),
                    database.repository().findByContinent(" Europe "));
            CapitalCommandResult result = CapitalCommandResult.run(database.repository(),
                    "--capitals-continent", "North America");
            assertEquals(0, result.status());
            assertEquals("Name | Country | Population\nOttawa | Canada | 2,000,000\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void treatsUnknownAndSqlLikeInputAsLiteralFilterValues() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            for (String area : List.of("Unknown continent", "Europe' OR '1'='1")) {
                CapitalCommandResult result = CapitalCommandResult.run(database.repository(),
                        "--capitals-continent", area);
                assertEquals(0, result.status());
                assertEquals("No capital cities found.\n", result.output());
            }
        }
    }

    @Test
    void rejectsMissingBlankAndExtraArgumentsBeforeConnecting() {
        CapitalCityRepository repository = new CapitalCityRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });
        for (String[] args : List.of(new String[]{"--capitals-continent"},
                new String[]{"--capitals-continent", " "},
                new String[]{"--capitals-continent", "Europe", "extra"})) {
            CapitalCommandResult result = CapitalCommandResult.run(repository, args);
            assertEquals(2, result.status());
            assertEquals("", result.output());
            assertTrue(!result.errors().isBlank());
        }
    }

    @Test
    void supportsContinentNamesContainingSpaces() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            database.execute("UPDATE country SET Continent = 'North America' WHERE Code = 'CAN'");
            CapitalCommandResult result = CapitalCommandResult.run(database.repository(),
                    "--capitals-continent", "North America");
            assertEquals(0, result.status());
            assertTrue(result.output().contains("Ottawa | Canada"));
        }
    }
}
