package com.napier.group9;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

/** Exit codes: 0 success, 1 database error, 2 invalid input. */
final class LanguagePopulationCommand {
    private LanguagePopulationCommand() {
    }

    static boolean supports(String command) {
        return "PB-32".equals(command)
                || "--language-population".equals(command);
    }

    static String usage() {
        return "Language population: PB-32 (or --language-population)";
    }

    static int run(String[] args, LanguagePopulationRepository repository,
                   PrintStream output, PrintStream errors) {
        if (args.length != 1 || !supports(args[0])) {
            errors.println(usage());
            return 2;
        }

        try {
            List<LanguagePopulation> languages = repository.findFiveLanguages();
            LanguagePopulationFormatter.print(languages, output);
            return 0;
        } catch (SQLException exception) {
            errors.println(
                    "Could not generate language population report. "
                            + "Check the database connection and world data.");
            return 1;
        }
    }
}