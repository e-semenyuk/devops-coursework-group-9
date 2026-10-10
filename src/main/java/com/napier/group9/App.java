package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/** Application entry point for the Population Reporting System. */
public final class App {
    private App() {
    }

    public static void main(String[] args) {
        DatabaseConfig config = DatabaseConfig.fromEnvironment();

        if (args.length > 0 && CountryReportCommand.supports(args[0])) {
            int status = CountryReportCommand.run(args,
                    new CountryPopulationRepository(config), System.out, System.err);
            if (status != 0) {
                System.exit(status);
            }
            return;
        }

        if (args.length > 0 && LanguagePopulationCommand.supports(args[0])) {
            int status = LanguagePopulationCommand.run(args,
                    new LanguagePopulationRepository(config), System.out, System.err);
            if (status != 0) {
                System.exit(status);
            }
            return;
        }

        if (args.length > 0 && PopulationDistributionCommand.supports(args[0])) {
            int status = PopulationDistributionCommand.run(args,
                    new PopulationDistributionRepository(config), System.out, System.err);
            if (status != 0) {
                System.exit(status);
            }
            return;
        }

        if (args.length > 0 && PopulationTotalCommand.supports(args[0])) {
            int status = PopulationTotalCommand.run(args,
                    new PopulationTotalRepository(config), System.out, System.err);
            if (status != 0) {
                System.exit(status);
            }
            return;
        }

        if (args.length > 0 && CityReportCommand.supports(args[0])) {
            int status = CityReportCommand.run(args,
                    new CityPopulationRepository(config), System.out, System.err);
            if (status != 0) {
                System.exit(status);
            }
            return;
        }

        if (args.length > 0 && CapitalReportCommand.supports(args[0])) {
            int status = CapitalReportCommand.run(args,
                    new CapitalCityRepository(config), System.out, System.err);
            if (status != 0) {
                System.exit(status);
            }
            return;
        }

        if (args.length == 0) {
            System.out.println(CountryReportCommand.usage());
            System.out.println(LanguagePopulationCommand.usage());
            System.out.println(PopulationDistributionCommand.usage());
            System.out.println(PopulationTotalCommand.usage());
            System.out.println(CityReportCommand.usage());
            System.out.println(CapitalReportCommand.usage());
        }

        if (args.length == 1 && "--healthcheck".equals(args[0])) {
            verifyDatabase(config);
            return;
        }

        System.out.println("Population Reporting System — Group 9");
        System.out.printf("Database: %s:%d/%s as %s%n",
                config.host(), config.port(), config.database(), config.username());
        System.out.println("Project foundation is ready. Report commands will be implemented through assigned issues.");
    }

    private static void verifyDatabase(DatabaseConfig config) {
        try (Connection connection = DriverManager.getConnection(
                config.jdbcUrl(), config.username(), config.password());
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT 1")) {
            if (!result.next() || result.getInt(1) != 1) {
                throw new IllegalStateException("Unexpected database health-check response");
            }
            System.out.println("Database connection successful");
        } catch (Exception exception) {
            throw new IllegalStateException("Database connection failed", exception);
        }
    }
}
