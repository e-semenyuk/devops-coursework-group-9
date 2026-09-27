package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Reads capital cities from the world database, ordered by city population. */
public final class CapitalCityRepository {
    private final ConnectionFactory connections;

    public CapitalCityRepository(DatabaseConfig config) {
        this(() -> DriverManager.getConnection(
                config.jdbcUrl(), config.username(), config.password()));
    }

    CapitalCityRepository(ConnectionFactory connections) {
        this.connections = connections;
    }

    public List<CapitalCity> findAll() throws SQLException {
        String sql = """
                SELECT city.Name AS CapitalName, country.Name AS CountryName,
                       city.Population
                FROM country
                INNER JOIN city ON city.ID = country.Capital
                               AND city.CountryCode = country.Code
                ORDER BY city.Population DESC, city.Name ASC, country.Code ASC
                """;
        try (Connection connection = connections.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<CapitalCity> capitals = new ArrayList<>();
            while (result.next()) {
                capitals.add(new CapitalCity(result.getString("CapitalName").trim(),
                        result.getString("CountryName").trim(), result.getLong("Population")));
            }
            return capitals;
        }
    }

    @FunctionalInterface
    interface ConnectionFactory {
        Connection open() throws SQLException;
    }
}
