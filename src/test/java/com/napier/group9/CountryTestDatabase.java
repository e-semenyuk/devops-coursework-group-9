package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/** Test data, not real population figures. */
final class CountryTestDatabase implements AutoCloseable {
    private final String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL";
    private final Connection keeper = DriverManager.getConnection(url);

    CountryTestDatabase() throws SQLException {
        execute("""
                CREATE TABLE country (Code CHAR(3) PRIMARY KEY, Name VARCHAR(52),
                    Continent VARCHAR(30), Region VARCHAR(26), Population BIGINT, Capital INT);
                CREATE TABLE city (ID INT PRIMARY KEY, Name VARCHAR(35), CountryCode CHAR(3));
                INSERT INTO country VALUES
                    ('AAA', 'Alpha', 'Asia', 'East Asia', 5000000, 2),
                    ('BBB', 'Bravo', 'Asia', 'East Asia', 5000000, 3),
                    ('ZZZ', 'Zulu', 'Europe', 'Europe', 10000000, NULL),
                    ('BAD', 'Broken link', 'Africa', 'Central Africa', 1000, 4),
                    ('LOW', 'Low', 'Oceania', 'Australia', 200, NULL);
                INSERT INTO city VALUES
                    (2, 'Alpha City', 'AAA'),
                    (3, 'Bravo City', 'BBB'),
                    (4, 'Wrong Country City', 'AAA');
                """);
    }

    CountryPopulationRepository repository() {
        return new CountryPopulationRepository(() -> DriverManager.getConnection(url));
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
