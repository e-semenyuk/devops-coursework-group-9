package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class CountryPopulationRepository {
    private final ConnectionFactory connections;

    public CountryPopulationRepository(DatabaseConfig config) {
        this(() -> DriverManager.getConnection(config.jdbcUrl(), config.username(), config.password()));
    }

    CountryPopulationRepository(ConnectionFactory connections) {
        this.connections = connections;
    }

    public List<CountryPopulation> findAllByPopulation() throws SQLException {
        String sql = """
                SELECT country.Code, country.Name, country.Continent, country.Region,
                       country.Population, city.Name AS CapitalName
                FROM country
                LEFT JOIN city ON city.ID = country.Capital
                               AND city.CountryCode = country.Code
                ORDER BY country.Population DESC, country.Name ASC, country.Code ASC
                """;
        try (Connection connection = connections.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<CountryPopulation> countries = new ArrayList<>();
            while (result.next()) {
                countries.add(new CountryPopulation(
                        result.getString("Code").trim(),
                        result.getString("Name").trim(),
                        result.getString("Continent").trim(),
                        result.getString("Region").trim(),
                        result.getLong("Population"),
                        result.getString("CapitalName")));
            }
            return countries;
        }
    }

    @FunctionalInterface
    interface ConnectionFactory {
        Connection open() throws SQLException;
    }
}
