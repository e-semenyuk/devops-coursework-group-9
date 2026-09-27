package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/** Synthetic values, deliberately including large totals and inconsistent city data. */
final class DistributionTestDatabase implements AutoCloseable {
    private final String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL";
    private final Connection keeper = DriverManager.getConnection(url);

    DistributionTestDatabase() throws SQLException {
        execute("""
                CREATE TABLE country (Code CHAR(3) PRIMARY KEY, Name VARCHAR(52),
                    Continent VARCHAR(30), Region VARCHAR(26), Population BIGINT);
                CREATE TABLE city (ID INT PRIMARY KEY, CountryCode CHAR(3), Population BIGINT);
                INSERT INTO country VALUES
                    ('GBR', 'United Kingdom', 'Europe', 'British Islands', 3000000000),
                    ('IRL', 'Ireland', 'Europe', 'British Islands', 1000000000),
                    ('FRA', 'France', 'Europe', 'Western Europe', 1000000000),
                    ('NOC', 'No cities', 'Europe', 'Western Europe', 25),
                    ('CAN', 'Canada', 'North America', 'North America', 100),
                    ('BAD', 'Inconsistent example', 'North America', 'North America', 50),
                    ('ZER', 'Zero population', 'Antarctica', 'Antarctica', 0);
                INSERT INTO city VALUES
                    (1, 'GBR', 1000000000),
                    (2, 'GBR', 500000000),
                    (3, 'IRL', 200000000),
                    (4, 'FRA', 800000000),
                    (5, 'CAN', 25),
                    (6, 'BAD', 75),
                    (7, 'XXX', 999999999);
                """);
    }

    PopulationDistributionRepository repository() {
        return new PopulationDistributionRepository(() -> DriverManager.getConnection(url));
    }

    void execute(String sql) throws SQLException {
        try (Statement statement = keeper.createStatement()) {
            statement.execute(sql);
        }
    }

    @Override
    public void close() throws SQLException {
        keeper.close();
    }
}

