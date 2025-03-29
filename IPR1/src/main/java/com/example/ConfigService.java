package com.example;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigService {
    private Map<String, Integer> serviceDelays = new HashMap<>();
    private Map<String, Integer> serviceErrorRates = new HashMap<>();

    public ConfigService() {
        serviceDelays.put("default setting Delay", 0); // Задержка по умолчанию
        serviceErrorRates.put("default setting Error Rates", 0); // Вероятность ошибки по умолчанию
    }

    public void setServiceDelay(String serviceName, int delayMs) {
        serviceDelays.put(serviceName, delayMs);
    }

    public void setServiceErrorRate(String serviceName, int errorRate) {
        serviceErrorRates.put(serviceName, errorRate);
    }

    public int getServiceDelay(String serviceName) {
        return serviceDelays.getOrDefault(serviceName, 0);
    }

    public int getServiceErrorRate(String serviceName) {
        return serviceErrorRates.getOrDefault(serviceName, 0);
    }

    public Map<String, Map<String, Integer>> getAllConfigs() {
        Map<String, Map<String, Integer>> configs = new HashMap<>();
        configs.put("delays", new HashMap<>(serviceDelays));
        configs.put("errorRates", new HashMap<>(serviceErrorRates));
        return configs;
    }
}