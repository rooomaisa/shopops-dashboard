package com.shopops.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "ok");
        response.put("service", "shopops-api");
        response.put("database", checkDatabase(response));
        return response;
    }

    private String checkDatabase(Map<String, String> response) {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2) ? "connected" : "disconnected";
        } catch (Exception ex) {
            response.put("databaseError", ex.getMessage());
            return "disconnected";
        }
    }
}
