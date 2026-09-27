package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class CapitalWorldReportTest {
    @Test
    void joinsOnlyMatchingCapitalsAndRanksCityPopulationsWithStableTies() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            assertEquals(List.of(new CapitalCity("London", "United Kingdom", 3000000),
                    new CapitalCity("Dublin", "Ireland", 2000000),
                    new CapitalCity("Ottawa", "Canada", 2000000)), database.repository().findAll());
        }
    }

    @Test
    void rendersRequiredColumnsThroughCommand() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayOutputStream errors = new ByteArrayOutputStream();
            int status = CapitalReportCommand.run(new String[]{"--capitals-world"},
                    database.repository(), stream(output), stream(errors));
            assertEquals(0, status);
            assertEquals("", errors.toString(StandardCharsets.UTF_8));
            assertEquals("Name | Country | Population\nLondon | United Kingdom | 3,000,000\n"
                    + "Dublin | Ireland | 2,000,000\nOttawa | Canada | 2,000,000\n", text(output));
        }
    }

    @Test
    void reportsEmptyDataWithoutPrintingAnEmptyTable() throws Exception {
        try (CapitalTestDatabase database = new CapitalTestDatabase()) {
            database.execute("DELETE FROM city");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            assertEquals(0, CapitalReportCommand.run(new String[]{"--capitals-world"},
                    database.repository(), stream(output), System.err));
            assertEquals("No capital cities found.\n", text(output));
        }
    }

    @Test
    void rejectsExtraArgumentsBeforeOpeningDatabase() {
        CapitalCityRepository repository = new CapitalCityRepository(() -> {
            throw new AssertionError("Invalid input must not access the database");
        });
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        assertEquals(2, CapitalReportCommand.run(new String[]{"--capitals-world", "extra"},
                repository, System.out, stream(errors)));
        assertTrue(text(errors).contains("--capitals-world"));
    }

    @Test
    void reportsDatabaseFailureWithoutLeakingDriverDetails() {
        CapitalCityRepository repository = new CapitalCityRepository(() -> {
            throw new SQLException("secret password and private host");
        });
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        assertEquals(1, CapitalReportCommand.run(new String[]{"--capitals-world"},
                repository, stream(output), stream(errors)));
        assertEquals("", text(output));
        assertEquals("Could not generate capital-city report. Check the database connection and world data.\n",
                text(errors));
    }

    @Test
    void formatsUnicodeAndLargePopulationsIndependentlyOfMachineLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            CapitalReportFormatter.print(List.of(new CapitalCity("Reykjavík", "Iceland", 3000000000L)),
                    stream(output));
            assertEquals("Name | Country | Population\nReykjavík | Iceland | 3,000,000,000\n", text(output));
        } finally {
            Locale.setDefault(previous);
        }
    }

    private static PrintStream stream(ByteArrayOutputStream output) {
        return new PrintStream(output, true, StandardCharsets.UTF_8);
    }

    private static String text(ByteArrayOutputStream output) {
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
