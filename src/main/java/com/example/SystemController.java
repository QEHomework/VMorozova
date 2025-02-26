package com.example;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

@RestController
public class SystemController implements HealthIndicator {

    @Autowired
    private ConfigService configService;

    @GetMapping("/config")
    public Map<String, Map<String, Integer>> getConfig() {
        return configService.getAllConfigs();
    }

    @Override
    public Health health() {
        return Health.up().build();
    }
}