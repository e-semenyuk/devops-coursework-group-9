package com.napier.group9;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

/** Validates capital-report arguments and returns a process exit status. */
final class CapitalReportCommand {
    private CapitalReportCommand() {
    }

    static boolean supports(String command) {
        return "--capitals-world".equals(command);
    }

    static String usage() {
        return "Capital report: --capitals-world";
    }

    static int run(String[] args, CapitalCityRepository repository,
                   PrintStream output, PrintStream errors) {
        try {
            if (args.length != 1 || !supports(args[0])) {
                throw new IllegalArgumentException(usage());
            }
            List<CapitalCity> capitals = repository.findAll();
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
}
