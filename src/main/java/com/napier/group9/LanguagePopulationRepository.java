package com.napier.group9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Retrieves population information for the five languages required by PB-32. */
public final class LanguagePopulationRepository {
    private final ConnectionFactory connections;

    public LanguagePopulationRepository(DatabaseConfig config) {
        this(() -> DriverManager.getConnection(
                config.jdbcUrl(), config.username(), config.password()));
    }

    LanguagePopulationRepository(ConnectionFactory connections) {
        this.connections = connections;
    }

    /**
     * Returns population information for Chinese, English, Hindi, Spanish,
     * and Arabic, ordered from greatest to smallest number of speakers.
     *
     * @return the five required language population results
     * @throws SQLException if the database query fails
     */
    public List<LanguagePopulation> findFiveLanguages() throws SQLException {
        String sql = """
                SELECT cl.Language,
                       ROUND(SUM(c.Population * cl.Percentage / 100)) AS Speakers,
                       (SELECT SUM(Population) FROM country) AS WorldPopulation
                FROM countrylanguage cl
                JOIN country c ON c.Code = cl.CountryCode
                WHERE cl.Language IN (?, ?, ?, ?, ?)
                GROUP BY cl.Language
                ORDER BY Speakers DESC
                """;

        try (Connection connection = connections.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "Chinese");
            statement.setString(2, "English");
            statement.setString(3, "Hindi");
            statement.setString(4, "Spanish");
            statement.setString(5, "Arabic");

            try (ResultSet result = statement.executeQuery()) {
                List<LanguagePopulation> languages = new ArrayList<>();

                while (result.next()) {
                    languages.add(new LanguagePopulation(
                            result.getString("Language").trim(),
                            result.getLong("Speakers"),
                            result.getLong("WorldPopulation")));
                }

                return languages;
            }
        }
    }

    @FunctionalInterface
    interface ConnectionFactory {
        Connection open() throws SQLException;
    }
}