package com.sbms.boarding_service.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BoardingDatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    // Inject JdbcTemplate to interact with the DB
    public BoardingDatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        if (checkDatabase()) {
            return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("status", "Reachable")
                    .build();
        }
        return Health.down()
                .withDetail("database", "PostgreSQL")
                .withDetail("error", "Not reachable")
                .build();
    }

    private boolean checkDatabase() {
        try {
            // Executes a simple query to verify connection
            jdbcTemplate.execute("SELECT 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}