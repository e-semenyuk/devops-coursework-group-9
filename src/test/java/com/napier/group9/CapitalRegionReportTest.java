package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CapitalRegionReportTest {
    @Test
    void filtersRegionWithinAContinentAndHandlesSpaces() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            // Same continent, different region: continent filtering would be incorrect here.
            database.execute("UPDATE country SET Continent = 'Europe' WHERE Code = 'CAN'");
            assertEquals(List.of(new CapitalCity("London", "United Kingdom", 3000000),
                    new CapitalCity("Dublin", "Ireland", 2000000)),
                    database.repository().findByRegion(" British Islands "));
            CapitalCommandResult result = CapitalCommandResult.run(database.repository(),
                    "--capitals-region", "British Islands");
            assertEquals(0, result.status());
            assertEquals("Name | Country | Population\nLondon | United Kingdom | 3,000,000\n"
                    + "Dublin | Ireland | 2,000,000\n", result.output());
            assertEquals("", result.errors());
        }
    }

    @Test
    void treatsUnknownAndSqlLikeRegionsAsLiteralValues() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            for (String area : List.of("Unknown region", "British Islands' OR '1'='1")) {
                CapitalCommandResult result = CapitalCommandResult.run(database.repository(),
                        "--capitals-region", area);
                assertEquals(0, result.status());
                assertEquals("No capital cities found.\n", result.output());
            }
        }
    }

    @Test
    void bindsRegionNamesContainingAnApostrophe() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            database.execute("UPDATE country SET Region = 'King''s region' WHERE Code = 'GBR'");
            assertEquals(List.of(new CapitalCity("London", "United Kingdom", 3000000)),
                    database.repository().findByRegion("King's region"));
        }
    }

    @Test
    void rejectsMissingBlankAndExtraArgumentsBeforeConnecting() {
        CapitalCityRepository repository = new CapitalCityRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });
        for (String[] args : List.of(new String[]{"--capitals-region"},
                new String[]{"--capitals-region", " "},
                new String[]{"--capitals-region", "British Islands", "extra"})) {
            CapitalCommandResult result = CapitalCommandResult.run(repository, args);
            assertEquals(2, result.status());
            assertEquals("", result.output());
            assertTrue(!result.errors().isBlank());
        }
    }
}
