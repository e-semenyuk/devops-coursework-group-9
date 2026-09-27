package com.napier.group9;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

/** Validates capital-report arguments and returns a process exit status. */
final class CapitalReportCommand {
    private CapitalReportCommand() {
    }

    static boolean supports(String command) {
        return "--capitals-world".equals(command) || "--capitals-continent".equals(command)
                || "--capitals-region".equals(command);
    }

    static String usage() {
        return "Capital reports: --capitals-world | --capitals-continent <continent>"
                + " | --capitals-region <region>";
    }

    static int run(String[] args, CapitalCityRepository repository,
                   PrintStream output, PrintStream errors) {
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException(usage());
            }
            List<CapitalCity> capitals = switch (args[0]) {
                case "--capitals-world" -> {
                    requireArguments(args, 1);
                    yield repository.findAll();
                }
                case "--capitals-continent" -> {
                    requireArguments(args, 2);
                    yield repository.findByContinent(args[1]);
                }
                case "--capitals-region" -> {
                    requireArguments(args, 2);
                    yield repository.findByRegion(args[1]);
                }
                default -> throw new IllegalArgumentException(usage());
            };
            CapitalReportFormatter.print(capitals, output);
            return 0;
        } catch (IllegalArgumentException exception) {
            errors.println(exception.getMessage());
            return 2;
        } catch (SQLException exception) {
            // Driver messages may contain connection details; do not expose them.
            errors.println("Could not generate capital-city report. Check the database connection and world data.");
            return 1;
        }
    }

    private static void requireArguments(String[] args, int count) {
        if (args.length != count) {
            throw new IllegalArgumentException(usage());
        }
    }
}
