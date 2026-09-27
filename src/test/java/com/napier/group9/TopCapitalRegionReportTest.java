package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TopCapitalRegionReportTest {
    @Test
    void filtersByRegionBeforeLimitingEvenWhenOtherRegionsHaveLargerCapitals() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            database.execute("UPDATE country SET Continent = 'Europe' WHERE Code = 'CAN';"
                    + "UPDATE city SET Population = 99000000 WHERE CountryCode = 'CAN'");
            assertEquals(List.of(new CapitalCity("London", "United Kingdom", 3000000)),
                    database.repository().findTopByRegion("British Islands", 1));
            CapitalCommandResult result = CapitalCommandResult.run(database.repository(),
                    "--top-capitals-region", " British Islands ", "1");
            assertEquals(0, result.status());
            assertEquals("Name | Country | Population\nLondon | United Kingdom | 3,000,000\n",
                    result.output());
        }
    }

    @Test
    void handlesFewerMatchesUnknownAndSqlLikeRegions() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            assertEquals(database.repository().findByRegion("British Islands"),
                    database.repository().findTopByRegion("British Islands", 20));
            for (String area : List.of("Unknown region", "British Islands' OR '1'='1")) {
                CapitalCommandResult result = CapitalCommandResult.run(database.repository(),
                        "--top-capitals-region", area, "3");
                assertEquals(0, result.status());
                assertEquals("No capital cities found.\n", result.output());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "two", "1.5", "2147483648", "", " "})
    void rejectsInvalidNBeforeConnecting(String value) {
        CapitalCommandResult result = CapitalCommandResult.run(noConnection(),
                "--top-capitals-region", "British Islands", value);
        assertEquals(2, result.status());
        assertEquals("", result.output());
        assertTrue(result.errors().contains("positive integer"));
    }

    @Test
    void rejectsBlankRegionAndIncorrectArgumentCountsBeforeConnecting() {
        for (String[] args : List.of(new String[]{"--top-capitals-region"},
                new String[]{"--top-capitals-region", "British Islands"},
                new String[]{"--top-capitals-region", " ", "2"},
                new String[]{"--top-capitals-region", "British Islands", "2", "extra"})) {
            CapitalCommandResult result = CapitalCommandResult.run(noConnection(), args);
            assertEquals(2, result.status());
            assertEquals("", result.output());
            assertTrue(!result.errors().isBlank());
        }
    }

    private static CapitalCityRepository noConnection() {
        return new CapitalCityRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });
    }
}
