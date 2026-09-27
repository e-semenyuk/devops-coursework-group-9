package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Aggregates cities per country before joining, so country totals are counted once. */
public final class PopulationDistributionRepository {
    private final ConnectionFactory connections;

    public PopulationDistributionRepository(DatabaseConfig config) {
        this(() -> DriverManager.getConnection(config.jdbcUrl(), config.username(), config.password()));
    }

    PopulationDistributionRepository(ConnectionFactory connections) {
        this.connections = connections;
    }

    public List<PopulationDistribution> findByContinent() throws SQLException {
        return find("country.Continent", "country.Continent");
    }

    public List<PopulationDistribution> findByRegion() throws SQLException {
        return find("country.Region", "country.Region");
    }

    // Expressions are fixed by the report methods, never supplied by the caller.
    private List<PopulationDistribution> find(String nameColumn, String groupColumns) throws SQLException {
        String sql = "SELECT " + nameColumn + """
                 AS AreaName, SUM(country.Population) AS TotalPopulation,
                       SUM(COALESCE(city_totals.Population, 0)) AS CityPopulation
                FROM country
                LEFT JOIN (
                    SELECT CountryCode, SUM(Population) AS Population
                    FROM city GROUP BY CountryCode
                ) city_totals ON city_totals.CountryCode = country.Code
                GROUP BY
                """ + groupColumns + " ORDER BY CAST(" + nameColumn + " AS CHAR(52)), " + groupColumns;
        try (Connection connection = connections.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<PopulationDistribution> groups = new ArrayList<>();
            while (result.next()) {
                groups.add(new PopulationDistribution(result.getString("AreaName").trim(),
                        result.getLong("TotalPopulation"), result.getLong("CityPopulation")));
            }
            return groups;
        }
    }

    @FunctionalInterface
    interface ConnectionFactory {
        Connection open() throws SQLException;
    }
}
