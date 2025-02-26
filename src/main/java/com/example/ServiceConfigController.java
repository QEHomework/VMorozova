package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceConfigController {

    @Autowired
    private ConfigService сonfigService;

    @GetMapping("/setDelay")
    public String setDelay(@RequestParam String serviceName, @RequestParam int delayMs) {
        сonfigService.setServiceDelay(serviceName, delayMs);
        return "Delay set to " + delayMs + " ms for service " + serviceName;
    }

    @GetMapping("/setErrorRate")
    public String setErrorRate(@RequestParam String serviceName, @RequestParam int errorRate) {
        сonfigService.setServiceErrorRate(serviceName, errorRate);
        return "Error rate set to " + errorRate + "% for service " + serviceName;
    }
}