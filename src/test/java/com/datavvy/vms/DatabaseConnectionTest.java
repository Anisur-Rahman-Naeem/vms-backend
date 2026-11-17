package com.datavvy.vms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("Verify that DataSource is injected and database connection is healthy")
    void testDatabaseConnectivity() throws SQLException {
        assertNotNull(dataSource, "DataSource bean must be configured");

        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection from DataSource should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            assertTrue(connection.isValid(2), "Database connection should be valid");
        }
    }
}

