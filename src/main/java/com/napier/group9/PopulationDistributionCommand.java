package com.napier.group9;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

/** Runs distribution reports; 0 = success, 2 = invalid input, 1 = database failure. */
final class PopulationDistributionCommand {
    private PopulationDistributionCommand() {
    }

    static boolean supports(String command) {
        return "PB-23".equals(command) || "--population-by-continent".equals(command)
                || "PB-24".equals(command) || "--population-by-region".equals(command);
    }

    static String usage() {
        return "Population distribution: PB-23 (or --population-by-continent)"
                + " | PB-24 (or --population-by-region)";
    }

    static int run(String[] args, PopulationDistributionRepository repository,
                   PrintStream output, PrintStream errors) {
        if (args.length != 1 || !supports(args[0])) {
            errors.println(usage());
            return 2;
        }
        try {
            List<PopulationDistribution> groups = switch (args[0]) {
                case "PB-23", "--population-by-continent" -> repository.findByContinent();
                case "PB-24", "--population-by-region" -> repository.findByRegion();
                default -> throw new IllegalArgumentException(usage());
            };
            PopulationDistributionFormatter.print(groups, output);
            return 0;
        } catch (SQLException exception) {
            errors.println("Could not generate population distribution. Check the database connection and world data.");
            return 1;
        }
    }
}
