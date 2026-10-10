package com.napier.group9;

import java.io.PrintStream;
import java.sql.SQLException;

/** Exit codes: 0 success, 1 database error, 2 invalid input. */
final class CountryReportCommand {
    private CountryReportCommand() {
    }

    static boolean supports(String command) {
        return "PB-01".equals(command) || "--countries-world".equals(command);
    }

    static String usage() {
        return "Country report: PB-01 (or --countries-world)";
    }

    static int run(String[] args, CountryPopulationRepository repository,
                   PrintStream output, PrintStream errors) {
        try {
            if (args.length != 1 || !supports(args[0])) {
                throw new IllegalArgumentException(usage());
            }
            CountryReportFormatter.print(repository.findAllByPopulation(), output);
            return 0;
        } catch (IllegalArgumentException exception) {
            errors.println(exception.getMessage());
            return 2;
        } catch (SQLException exception) {
            errors.println("Could not generate country report. Check the database connection and world data.");
            return 1;
        }
    }
}
