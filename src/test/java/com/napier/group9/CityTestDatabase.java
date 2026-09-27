package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/** Test data, not real population figures. */
final class CityTestDatabase implements AutoCloseable {
    private final String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL";
    private final Connection keeper = DriverManager.getConnection(url);

    CityTestDatabase() throws SQLException {
        execute("""
                CREATE TABLE country (Code CHAR(3) PRIMARY KEY, Name VARCHAR(52),
                    Continent VARCHAR(30), Region VARCHAR(26));
                CREATE TABLE city (ID INT PRIMARY KEY, Name VARCHAR(35),
                    CountryCode CHAR(3), District VARCHAR(20), Population BIGINT);
                INSERT INTO country VALUES
                    ('GBR', 'United Kingdom', 'Europe', 'British Islands'),
                    ('IRL', 'Ireland', 'Europe', 'British Islands'),
                    ('FRA', 'France', 'Europe', 'Western Europe'),
                    ('CAN', 'Canada', 'North America', 'North America');
                INSERT INTO city VALUES
                    (1, 'Edinburgh', 'GBR', 'Scotland', 400000),
                    (2, 'Glasgow', 'GBR', 'Scotland', 600000),
                    (3, 'Aberdeen', 'GBR', 'Scotland', 200000),
                    (4, 'London', 'GBR', 'England', 3000000),
                    (5, 'Dublin', 'IRL', 'Leinster', 500000),
                    (6, 'Paris', 'FRA', 'Île-de-France', 2000000),
                    (7, 'Toronto', 'CAN', 'Ontario', 99000000),
                    (8, 'Dundee', 'GBR', 'Scotland', 200000),
                    (9, 'Orphan', 'ZZZ', 'Scotland', 99999999);
                """);
    }

    CityPopulationRepository repository() {
        return new CityPopulationRepository(() -> DriverManager.getConnection(url));
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
