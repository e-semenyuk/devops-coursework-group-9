package com.napier.group9;

import java.util.Map;

/** Immutable database connection settings loaded from environment variables. */
public record DatabaseConfig(
        String host,
        int port,
        String database,
        String username,
        String password) {

    public static DatabaseConfig fromEnvironment() {
        return fromEnvironment(System.getenv());
    }

    static DatabaseConfig fromEnvironment(Map<String, String> environment) {
        return new DatabaseConfig(
                environment.getOrDefault("DB_HOST", "localhost"),
                parsePort(environment.getOrDefault("DB_PORT", "3306")),
                environment.getOrDefault("DB_NAME", "world"),
                environment.getOrDefault("DB_USER", "root"),
                environment.getOrDefault("DB_PASSWORD", "example"));
    }

    public String jdbcUrl() {
        return "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true"
                .formatted(host, port, database);
    }

    private static int parsePort(String value) {
        try {
            int port = Integer.parseInt(value);
            if (port < 1 || port > 65_535) {
                throw new IllegalArgumentException("DB_PORT must be between 1 and 65535");
            }
            return port;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("DB_PORT must be a number", exception);
        }
    }
}
