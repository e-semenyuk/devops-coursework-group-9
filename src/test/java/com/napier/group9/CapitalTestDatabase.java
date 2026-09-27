package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/** Synthetic test populations, not real statistics; UK/Ireland examples plus Canada to test filtering. */
final class CapitalTestDatabase implements AutoCloseable {
    private final String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL";
    private final Connection keeper = DriverManager.getConnection(url);

    CapitalTestDatabase() throws SQLException {
        execute("""
                CREATE TABLE country (Code CHAR(3) PRIMARY KEY, Name VARCHAR(52),
                    Continent VARCHAR(30), Region VARCHAR(26), Population BIGINT, Capital INT);
                CREATE TABLE city (ID INT PRIMARY KEY, Name VARCHAR(35),
                    CountryCode CHAR(3), Population BIGINT);
                INSERT INTO country VALUES
                    ('GBR', 'United Kingdom', 'Europe', 'British Islands', 9000000, 1),
                    ('IRL', 'Ireland', 'Europe', 'British Islands', 50000000, 2),
                    ('CAN', 'Canada', 'North America', 'North America', 100000000, 3),
                    ('ZZZ', 'No capital', 'Europe', 'British Islands', 200000000, NULL),
                    ('BAD', 'Missing capital', 'Europe', 'British Islands', 300000000, 999),
                    ('XXX', 'Wrong country link', 'Europe', 'British Islands', 400000000, 1);
                INSERT INTO city VALUES
                    (1, 'London', 'GBR', 3000000),
                    (2, 'Dublin', 'IRL', 2000000),
                    (3, 'Ottawa', 'CAN', 2000000),
                    (4, 'Not a capital', 'GBR', 99000000);
                """);
    }

    CapitalCityRepository repository() {
        return new CapitalCityRepository(() -> DriverManager.getConnection(url));
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
