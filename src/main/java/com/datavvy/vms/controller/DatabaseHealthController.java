package com.datavvy.vms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class DatabaseHealthController {

    private final DataSource dataSource;

    public DatabaseHealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "vms-backend");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            Map<String, Object> dbDetails = new LinkedHashMap<>();
            dbDetails.put("status", "CONNECTED");
            dbDetails.put("databaseProduct", metaData.getDatabaseProductName());
            dbDetails.put("databaseVersion", metaData.getDatabaseProductVersion());
            dbDetails.put("driverName", metaData.getDriverName());

            response.put("status", "UP");
            response.put("database", dbDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> dbDetails = new LinkedHashMap<>();
            dbDetails.put("status", "DISCONNECTED");
            dbDetails.put("error", e.getMessage());

            response.put("status", "DOWN");
            response.put("database", dbDetails);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}

