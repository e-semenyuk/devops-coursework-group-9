package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DatabaseConfigTest {
    @Test
    void usesSafeDevelopmentDefaults() {
        DatabaseConfig config = DatabaseConfig.fromEnvironment(Map.of());

        assertEquals("localhost", config.host());
        assertEquals(3306, config.port());
        assertEquals("world", config.database());
        assertEquals("root", config.username());
        assertEquals("jdbc:mysql://localhost:3306/world?useSSL=false&allowPublicKeyRetrieval=true",
                config.jdbcUrl());
    }

    @Test
    void readsConfiguredValues() {
        DatabaseConfig config = DatabaseConfig.fromEnvironment(Map.of(
                "DB_HOST", "db",
                "DB_PORT", "3307",
                "DB_NAME", "population",
                "DB_USER", "reporter",
                "DB_PASSWORD", "secret"));

        assertEquals("db", config.host());
        assertEquals(3307, config.port());
        assertEquals("population", config.database());
        assertEquals("reporter", config.username());
    }

    @Test
    void rejectsInvalidPort() {
        assertThrows(IllegalArgumentException.class,
                () -> DatabaseConfig.fromEnvironment(Map.of("DB_PORT", "invalid")));
    }
}
