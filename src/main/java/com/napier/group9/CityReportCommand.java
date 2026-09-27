package com.napier.group9;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

/** Validates city-report arguments; 0 = success, 2 = invalid input, 1 = database failure. */
final class CityReportCommand {
    private CityReportCommand() {
    }

    static boolean supports(String command) {
        return "--top-cities-continent".equals(command) || "PB-13".equals(command)
                || "--top-cities-region".equals(command) || "PB-14".equals(command);
    }

    static String usage() {
        return "City reports: PB-13 (or --top-cities-continent) <continent> <N> | PB-14 (or --top-cities-region) <region> <N>";
    }

    static int run(String[] args, CityPopulationRepository repository,
                   PrintStream output, PrintStream errors) {
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException(usage());
            }
            List<CityPopulation> cities = switch (args[0]) {
                case "PB-13", "--top-cities-continent" -> {
                    requireArguments(args, 3);
                    yield repository.findTopByContinent(args[1], parseLimit(args[2]));
                }
                case "PB-14", "--top-cities-region" -> {
                    requireArguments(args, 3);
                    yield repository.findTopByRegion(args[1], parseLimit(args[2]));
                }
                default -> throw new IllegalArgumentException(usage());
            };
            CityReportFormatter.print(cities, output);
            return 0;
        } catch (IllegalArgumentException exception) {
            errors.println(exception.getMessage());
            return 2;
        } catch (SQLException exception) {
            errors.println("Could not generate city report. Check the database connection and world data.");
            return 1;
        }
    }

    private static void requireArguments(String[] args, int count) {
        if (args.length != count) {
            throw new IllegalArgumentException(usage());
        }
    }

    private static int parseLimit(String value) {
        try {
            return Integer.parseInt(value.strip());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("N must be a positive integer between 1 and 2147483647.");
        }
    }
}
