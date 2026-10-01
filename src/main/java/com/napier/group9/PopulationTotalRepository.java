package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/** Retrieves population totals for selected geographic areas. */
public final class PopulationTotalRepository {
    private final ConnectionFactory connections;

    public PopulationTotalRepository(DatabaseConfig config) {
        this(() -> DriverManager.getConnection(
                config.jdbcUrl(), config.username(), config.password()));
    }

    PopulationTotalRepository(ConnectionFactory connections) {
        this.connections = connections;
    }

    /**
     * Returns the total population of a selected district.
     *
     * @param district district name
     * @return the district population, or empty when the district has no matching cities
     * @throws SQLException if the database query fails
     */
    public Optional<PopulationTotal> findByDistrict(String district) throws SQLException {
        String selectedDistrict = requireArea(district, "District");

        String sql = """
                SELECT city.District AS AreaName,
                       SUM(city.Population) AS TotalPopulation
                FROM city
                WHERE city.District = ?
                GROUP BY city.District
                """;

        try (Connection connection = connections.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, selectedDistrict);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                return Optional.of(new PopulationTotal(
                        result.getString("AreaName").trim(),
                        result.getLong("TotalPopulation")));
            }
        }
    }

    /**
     * Returns the population of a selected city.
     *
     * @param city city name
     * @return the city population, or empty when no city has the supplied name
     * @throws SQLException if the database query fails
     */
    public Optional<PopulationTotal> findByCity(String city) throws SQLException {
        String selectedCity = requireArea(city, "City");

        String sql = """
                SELECT city.Name AS AreaName,
                       city.Population AS TotalPopulation
                FROM city
                WHERE city.Name = ?
                ORDER BY city.ID ASC
                LIMIT 1
                """;

        try (Connection connection = connections.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, selectedCity);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                return Optional.of(new PopulationTotal(
                        result.getString("AreaName").trim(),
                        result.getLong("TotalPopulation")));
            }
        }
    }
    /**
     * Returns the total population of the world.
     *
     * @return the world population
     * @throws SQLException if the database query fails
     */
    public Optional<PopulationTotal> findWorldPopulation() throws SQLException {
        String sql = """
            SELECT 'World' AS AreaName,
                   SUM(country.Population) AS TotalPopulation
            FROM country
            """;

        try (Connection connection = connections.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (!result.next()) {
                return Optional.empty();
            }

            return Optional.of(new PopulationTotal(
                    result.getString("AreaName").trim(),
                    result.getLong("TotalPopulation")));
        }
    }

    private static String requireArea(String area, String label) {
        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException(label + " must not be blank.");
        }
        return area.strip();
    }

    @FunctionalInterface
    interface ConnectionFactory {
        Connection open() throws SQLException;
    }
}
