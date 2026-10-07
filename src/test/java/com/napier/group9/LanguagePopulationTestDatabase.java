package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/** Test database for the PB-32 five-language population report. */
final class LanguagePopulationTestDatabase implements AutoCloseable {
    private final String url =
            "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL";
    private final Connection keeper = DriverManager.getConnection(url);

    LanguagePopulationTestDatabase() throws SQLException {
        execute("""
                CREATE TABLE country (
                    Code CHAR(3) PRIMARY KEY,
                    Name VARCHAR(52),
                    Population BIGINT
                );

                CREATE TABLE countrylanguage (
                    CountryCode CHAR(3),
                    Language VARCHAR(30),
                    IsOfficial CHAR(1),
                    Percentage DECIMAL(4,1),
                    PRIMARY KEY (CountryCode, Language)
                );

                INSERT INTO country VALUES
                    ('AAA', 'Country A', 1000000),
                    ('BBB', 'Country B', 2000000),
                    ('CCC', 'Country C', 3000000);

                INSERT INTO countrylanguage VALUES
                    ('AAA', 'Chinese', 'F', 50.0),
                    ('BBB', 'Chinese', 'F', 25.0),
                    ('AAA', 'English', 'F', 20.0),
                    ('BBB', 'English', 'F', 10.0),
                    ('CCC', 'Hindi', 'F', 10.0),
                    ('CCC', 'Spanish', 'F', 5.0),
                    ('CCC', 'Arabic', 'F', 2.0),
                    ('AAA', 'French', 'F', 90.0);
                """);
    }

    LanguagePopulationRepository repository() {
        return new LanguagePopulationRepository(
                () -> DriverManager.getConnection(url));
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