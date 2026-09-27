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
        return find("", null, null);
    }

    public List<CapitalCity> findByContinent(String continent) throws SQLException {
        return find("WHERE country.Continent = ?", requireArea(continent, "Continent"), null);
    }

    public List<CapitalCity> findByRegion(String region) throws SQLException {
        return find("WHERE country.Region = ?", requireArea(region, "Region"), null);
    }

    public List<CapitalCity> findTop(int limit) throws SQLException {
        return find("", null, requireLimit(limit));
    }

    public List<CapitalCity> findTopByContinent(String continent, int limit) throws SQLException {
        return find("WHERE country.Continent = ?", requireArea(continent, "Continent"), requireLimit(limit));
    }

    private static int requireLimit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("N must be a positive integer.");
        }
        return limit;
    }

    private static String requireArea(String area, String label) {
        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException(label + " must not be blank.");
        }
        return area.strip();
    }

    private List<CapitalCity> find(String filter, String area, Integer limit) throws SQLException {
        String sql = """
                SELECT city.Name AS CapitalName, country.Name AS CountryName,
                       city.Population
                FROM country
                INNER JOIN city ON city.ID = country.Capital
                               AND city.CountryCode = country.Code
                """ + filter + " ORDER BY city.Population DESC, city.Name ASC, country.Code ASC"
                + (limit == null ? "" : " LIMIT ?");
        try (Connection connection = connections.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int parameter = 1;
            if (area != null) {
                statement.setString(parameter++, area);
            }
            if (limit != null) {
                statement.setInt(parameter, limit);
            }
            try (ResultSet result = statement.executeQuery()) {
                List<CapitalCity> capitals = new ArrayList<>();
                while (result.next()) {
                    capitals.add(new CapitalCity(result.getString("CapitalName").trim(),
                            result.getString("CountryName").trim(), result.getLong("Population")));
                }
                return capitals;
            }
        }
    }

    @FunctionalInterface
    interface ConnectionFactory {
        Connection open() throws SQLException;
    }
}
