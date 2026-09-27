package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TopCapitalWorldReportTest {
    @Test
    void ranksBeforeLimitingAndUsesDeterministicTies() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            assertEquals(List.of(new CapitalCity("London", "United Kingdom", 3000000),
                    new CapitalCity("Dublin", "Ireland", 2000000)), database.repository().findTop(2));
            CapitalCommandResult result = CapitalCommandResult.run(database.repository(),
                    "--top-capitals-world", "1");
            assertEquals(0, result.status());
            assertEquals("Name | Country | Population\nLondon | United Kingdom | 3,000,000\n",
                    result.output());
        }
    }

    @Test
    void returnsAllAvailableRowsWhenNExceedsThePopulationOfTheResultSet() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            assertEquals(database.repository().findAll(), database.repository().findTop(Integer.MAX_VALUE));
            database.execute("DELETE FROM city");
            CapitalCommandResult result = CapitalCommandResult.run(database.repository(),
                    "--top-capitals-world", "5");
            assertEquals(0, result.status());
            assertEquals("No capital cities found.\n", result.output());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "two", "1.5", "2147483648", "", " "})
    void rejectsInvalidNBeforeConnecting(String value) {
        CapitalCommandResult result = CapitalCommandResult.run(noConnection(), "--top-capitals-world", value);
        assertEquals(2, result.status());
        assertEquals("", result.output());
        assertTrue(result.errors().contains("positive integer"));
    }

    @Test
    void rejectsIncorrectArgumentCountsBeforeConnecting() {
        assertEquals(2, CapitalCommandResult.run(noConnection(), "--top-capitals-world").status());
        assertEquals(2, CapitalCommandResult.run(noConnection(), "--top-capitals-world", "2", "extra").status());
        assertThrows(IllegalArgumentException.class, () -> noConnection().findTop(0));
        assertThrows(IllegalArgumentException.class, () -> noConnection().findTop(-1));
    }

    private static CapitalCityRepository noConnection() {
        return new CapitalCityRepository(() -> {
            throw new AssertionError("Invalid input must not connect");
        });
    }
}
