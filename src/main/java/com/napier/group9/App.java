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
