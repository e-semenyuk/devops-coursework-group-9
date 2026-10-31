package com.napier.group9;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

/** Exit codes: 0 success, 1 database error, 2 invalid input. */
final class CountryReportCommand {
    private CountryReportCommand() {
    }

    static boolean supports(String command) {
        return "PB-01".equals(command) || "--countries-world".equals(command)
            || "PB-02".equals(command) || "--countries-continent".equals(command);
    }

    static String usage() {
        return "Country reports: PB-01 (or --countries-world) | "
            + "PB-02 (or --countries-continent) <continent>";
    }

    static int run(String[] args, CountryPopulationRepository repository,
                   PrintStream output, PrintStream errors) {
        try {
            if (args.length == 0 || !supports(args[0])) {
                throw new IllegalArgumentException(usage());
            }
            List<CountryPopulation> countries = switch (args[0]) {
                case "PB-01", "--countries-world" -> {
                    requireArguments(args, 1);
                    yield repository.findAllByPopulation();
                }
                case "PB-02", "--countries-continent" -> {
                    requireArguments(args, 2);
                    yield repository.findByContinent(args[1]);
                }
                default -> throw new IllegalArgumentException(usage());
            };
            CountryReportFormatter.print(countries, output);
            return 0;
        } catch (IllegalArgumentException exception) {
            errors.println(exception.getMessage());
            return 2;
        } catch (SQLException exception) {
            errors.println("Could not generate country report. Check the database connection and world data.");
            return 1;
        }
    }

    private static void requireArguments(String[] args, int count) {
        if (args.length != count) {
            throw new IllegalArgumentException(usage());
        }
    }
}
