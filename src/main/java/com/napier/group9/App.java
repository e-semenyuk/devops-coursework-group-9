package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Application entry point for the Population Reporting System. */
public final class App {
    private App() {
    }

    public static void main(String[] args) {
        DatabaseConfig config = DatabaseConfig.fromEnvironment();

        if (args.length == 1 && "--healthcheck".equals(args[0])) {
            verifyDatabase(config);
            return;
        }

        if (args.length == 1 && "--countries-by-population".equals(args[0])) {
            try {
                printCountriesByPopulation(config);
            } catch (SQLException exception) {
                System.err.printf("Could not generate country population report: %s%n",
                        exception.getMessage());
                System.exit(1);
            }
            return;
        }

        System.out.println("Population Reporting System — Group 9");
        System.out.printf("Database: %s:%d/%s as %s%n",
                config.host(), config.port(), config.database(), config.username());
        System.out.println("Use --countries-by-population to list all countries by population.");
    }

    private static void printCountriesByPopulation(DatabaseConfig config) throws SQLException {
        String query = """
                SELECT country.Code, country.Name, country.Continent, country.Region,
                       country.Population, COALESCE(city.Name, '') AS Capital
                FROM country
                LEFT JOIN city ON city.ID = country.Capital
                ORDER BY country.Population DESC, country.Name ASC
                """;

        try (Connection connection = DriverManager.getConnection(
                config.jdbcUrl(), config.username(), config.password());
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {
            System.out.println("Code | Name | Continent | Region | Population | Capital");
            while (result.next()) {
                System.out.printf("%s | %s | %s | %s | %,d | %s%n",
                        result.getString("Code"),
                        result.getString("Name").trim(),
                        result.getString("Continent"),
                        result.getString("Region").trim(),
                        result.getLong("Population"),
                        result.getString("Capital").trim());
            }
        }
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
