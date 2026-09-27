package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Queries cities and their country names from the world database. */
public final class CityPopulationRepository {
    private final ConnectionFactory connections;

    public CityPopulationRepository(DatabaseConfig config) {
        this(() -> DriverManager.getConnection(config.jdbcUrl(), config.username(), config.password()));
    }

    CityPopulationRepository(ConnectionFactory connections) {
        this.connections = connections;
    }

    public List<CityPopulation> findTopByContinent(String continent, int limit) throws SQLException {
        return find("WHERE country.Continent = ?", requireArea(continent, "Continent"), requireLimit(limit));
    }

    public List<CityPopulation> findTopByRegion(String region, int limit) throws SQLException {
        return find("WHERE country.Region = ?", requireArea(region, "Region"), requireLimit(limit));
    }

    private static String requireArea(String area, String label) {
        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException(label + " must not be blank.");
        }
        return area.strip();
    }

    private static int requireLimit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("N must be a positive integer.");
        }
        return limit;
    }

    private List<CityPopulation> find(String filter, String area, int limit) throws SQLException {
        String sql = """
                SELECT city.Name AS CityName, country.Name AS CountryName,
                       city.District, city.Population
                FROM city
                INNER JOIN country ON country.Code = city.CountryCode
                """ + filter + " ORDER BY city.Population DESC, city.Name ASC, country.Code ASC, city.ID ASC"
                + " LIMIT ?";
        try (Connection connection = connections.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, area);
            statement.setInt(2, limit);
            try (ResultSet result = statement.executeQuery()) {
                List<CityPopulation> cities = new ArrayList<>();
                while (result.next()) {
                    cities.add(new CityPopulation(result.getString("CityName").trim(),
                            result.getString("CountryName").trim(), result.getString("District").trim(),
                            result.getLong("Population")));
                }
                return cities;
            }
        }
    }

    @FunctionalInterface
    interface ConnectionFactory {
        Connection open() throws SQLException;
    }
}
