package com.napier.group9;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Optional;

/** Exit codes: 0 success, 1 database error, 2 invalid input. */
final class PopulationTotalCommand {
    private PopulationTotalCommand() {
    }

    static boolean supports(String command) {
        return "PB-30".equals(command)
                || "--population-district".equals(command);
    }

    static String usage() {
        return "Population totals: PB-30 (or --population-district) <district>";
    }

    static int run(String[] args, PopulationTotalRepository repository,
                   PrintStream output, PrintStream errors) {
        try {
            if (args.length != 2 || !supports(args[0])) {
                throw new IllegalArgumentException(usage());
            }

            Optional<PopulationTotal> total =
                    repository.findByDistrict(args[1]);

            PopulationTotalFormatter.print(total, output);
            return 0;
        } catch (IllegalArgumentException exception) {
            errors.println(exception.getMessage());
            return 2;
        } catch (SQLException exception) {
            errors.println(
                    "Could not generate population total. Check the database connection and world data.");
            return 1;
        }
    }
}