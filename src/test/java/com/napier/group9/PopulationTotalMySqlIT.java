package com.napier.group9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

/** Verifies a repository query against the MySQL service used by CI. */
class PopulationTotalMySqlIT {

    @Test
    void readsTheWorldPopulationFromMySql() throws SQLException {
        DatabaseConfig config = DatabaseConfig.fromEnvironment();
        PopulationTotalRepository repository = new PopulationTotalRepository(config);

        var result = repository.findWorldPopulation();

        assertTrue(result.isPresent());
        assertEquals("World", result.orElseThrow().name());
        assertEquals(68_000_000L, result.orElseThrow().population());
    }
}
