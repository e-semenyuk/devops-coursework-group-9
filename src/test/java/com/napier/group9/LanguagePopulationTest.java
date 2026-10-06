package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

class LanguagePopulationTest {

    @Test
    void returnsFiveRequiredLanguagesInDescendingSpeakerOrder() throws Exception {
        try (LanguagePopulationTestDatabase database =
                     new LanguagePopulationTestDatabase()) {

            List<LanguagePopulation> results =
                    database.repository().findFiveLanguages();

            assertEquals(5, results.size());

            assertEquals("Chinese", results.get(0).language());
            assertEquals(1_000_000L, results.get(0).speakers());

            assertEquals("English", results.get(1).language());
            assertEquals(400_000L, results.get(1).speakers());

            assertEquals("Hindi", results.get(2).language());
            assertEquals(300_000L, results.get(2).speakers());

            assertEquals("Spanish", results.get(3).language());
            assertEquals(150_000L, results.get(3).speakers());

            assertEquals("Arabic", results.get(4).language());
            assertEquals(60_000L, results.get(4).speakers());

            assertFalse(results.stream()
                    .anyMatch(result -> "French".equals(result.language())));
        }
    }

    @Test
    void calculatesPercentageOfWorldPopulation() throws Exception {
        try (LanguagePopulationTestDatabase database =
                     new LanguagePopulationTestDatabase()) {

            List<LanguagePopulation> results =
                    database.repository().findFiveLanguages();

            assertEquals(6_000_000L, results.get(0).worldPopulation());
            assertEquals("16.67%", results.get(0).worldPercentage());
            assertEquals("6.67%", results.get(1).worldPercentage());
            assertEquals("5.00%", results.get(2).worldPercentage());
            assertEquals("2.50%", results.get(3).worldPercentage());
            assertEquals("1.00%", results.get(4).worldPercentage());
        }
    }

    @Test
    void formatsReportAndAcceptsBothCommands() throws Exception {
        try (LanguagePopulationTestDatabase database =
                     new LanguagePopulationTestDatabase()) {

            var pbResult = LanguagePopulationCommandResult.run(
                    database.repository(), "PB-32");

            var namedResult = LanguagePopulationCommandResult.run(
                    database.repository(), "--language-population");

            assertEquals(0, pbResult.status());
            assertEquals("", pbResult.errors());

            assertTrue(pbResult.output().contains(
                    "Language | Speakers | World population %"));
            assertTrue(pbResult.output().contains(
                    "Chinese | 1,000,000 | 16.67%"));
            assertTrue(pbResult.output().contains(
                    "Arabic | 60,000 | 1.00%"));

            assertEquals(pbResult, namedResult);
        }
    }

    @Test
    void rejectsUnexpectedArgumentsBeforeConnecting() {
        var unused = new LanguagePopulationRepository(() -> {
            throw new AssertionError("Invalid arguments must not connect");
        });

        var result = LanguagePopulationCommandResult.run(
                unused, "PB-32", "extra");

        assertEquals(2, result.status());
        assertTrue(result.errors().contains("PB-32"));
    }

    @Test
    void databaseErrorsDoNotLeakCredentials() {
        var broken = new LanguagePopulationRepository(() -> {
            throw new SQLException("password=private");
        });

        var result = LanguagePopulationCommandResult.run(
                broken, "PB-32");

        assertEquals(1, result.status());
        assertEquals("", result.output());
        assertFalse(result.errors().contains("private"));
    }
}