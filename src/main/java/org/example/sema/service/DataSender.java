package org.example.sema.service;

import org.example.sema.entity.SensorData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataSender {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendDataToLogstash(SensorData sensorData) {
        String url = "http://localhost:5044";  // URL Logstash

        // Vytvoření JSON objektu
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("value", sensorData.getValue());
        jsonData.put("timestamp", sensorData.getCreated_at().toString());

        // Odeslání dat
        restTemplate.postForObject(url, jsonData, String.class);
    }
}
