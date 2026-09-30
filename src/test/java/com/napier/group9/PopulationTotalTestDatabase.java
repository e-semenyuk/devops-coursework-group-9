package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/** Test database for selected-area population total reports. */
final class PopulationTotalTestDatabase implements AutoCloseable {
    private final String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL";
    private final Connection keeper = DriverManager.getConnection(url);

    PopulationTotalTestDatabase() throws SQLException {
        execute("""
                CREATE TABLE city (
                    ID INT PRIMARY KEY,
                    Name VARCHAR(35),
                    CountryCode CHAR(3),
                    District VARCHAR(20),
                    Population BIGINT
                );

                INSERT INTO city VALUES
                    (1, 'Belize City', 'BLZ', 'Belize', 50000),
                    (2, 'San Pedro', 'BLZ', 'Belize', 20000),
                    (3, 'Orange Walk Town', 'BLZ', 'Orange Walk', 15000),
                    (4, 'Another Town', 'BLZ', 'Orange Walk', 5000);
                """);
    }

    PopulationTotalRepository repository() {
        return new PopulationTotalRepository(
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