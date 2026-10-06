package com.napier.group9;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Optional;

/** Exit codes: 0 success, 1 database error, 2 invalid input. */
final class PopulationTotalCommand {
    private PopulationTotalCommand() {
    }

    static boolean supports(String command) {
        return "PB-26".equals(command)
                || "--population-world".equals(command)
                || "PB-30".equals(command)
                || "--population-district".equals(command)
                || "PB-31".equals(command)
                || "--population-city".equals(command);
    }

    static String usage() {
        return "Population totals: PB-26 (or --population-world) | "
                + "PB-30 (or --population-district) <district> | "
                + "PB-31 (or --population-city) <city>";
    }

    static int run(String[] args, PopulationTotalRepository repository,
                   PrintStream output, PrintStream errors) {
        try {
            if (args.length == 0 || !supports(args[0])) {
                throw new IllegalArgumentException(usage());
            }


            Optional<PopulationTotal> total = switch (args[0]) {
                case "PB-26", "--population-world" -> {
                    if (args.length != 1) {
                        throw new IllegalArgumentException(usage());
                    }
                    yield repository.findWorldPopulation();
                }

                case "PB-30", "--population-district" -> {
                    if (args.length != 2) {
                        throw new IllegalArgumentException(usage());
                    }
                    yield repository.findByDistrict(args[1]);
                }

                case "PB-31", "--population-city" -> {
                    if (args.length != 2) {
                        throw new IllegalArgumentException(usage());
                    }
                    yield repository.findByCity(args[1]);
                }

                default -> throw new IllegalArgumentException(usage());
            };

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
