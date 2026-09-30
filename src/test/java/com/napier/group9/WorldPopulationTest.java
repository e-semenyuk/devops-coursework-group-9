package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class WorldPopulationTest {

    @Test
    void returnsWorldPopulation() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var result = database.repository().findWorldPopulation().orElseThrow();

            assertEquals("World", result.name());
            assertEquals(6_000_000L, result.population());
        }
    }

    @Test
    void formatsWorldPopulationAndAcceptsBothCommands() throws Exception {
        try (PopulationTotalTestDatabase database = new PopulationTotalTestDatabase()) {
            var pbResult = PopulationTotalCommandResult.run(
                    database.repository(), "PB-26");
            var namedResult = PopulationTotalCommandResult.run(
                    database.repository(), "--population-world");

            assertEquals(0, pbResult.status());
            assertEquals("", pbResult.errors());
            assertTrue(pbResult.output().contains("Name | Population"));
            assertTrue(pbResult.output().contains("World |"));
            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void rejectsUnexpectedArgumentsBeforeConnecting() {
        var unused = new PopulationTotalRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });

        var result = PopulationTotalCommandResult.run(
                unused, "PB-26", "extra");

        assertEquals(2, result.status());
        assertTrue(result.errors().contains("PB-26"));
    }

    @Test
    void databaseErrorsDoNotLeakCredentials() {
        var broken = new PopulationTotalRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = PopulationTotalCommandResult.run(
                broken, "PB-26");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
    }
}
