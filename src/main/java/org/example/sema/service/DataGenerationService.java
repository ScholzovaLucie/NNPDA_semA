package org.example.sema.service;

import lombok.AllArgsConstructor;
import org.example.sema.entity.Sensor;
import org.example.sema.entity.SensorData;
import org.example.sema.repository.SensorDataRepository;
import org.example.sema.repository.SensorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DataGenerationService {

    private final SensorDataRepository sensorDataRepository;
    private final SensorRepository sensorRepository;
    private final LocationService locationService;

    @Scheduled(fixedRate = 5000)
    public void generateSensorData() {
        List<Sensor> sensors = sensorRepository.findAll();

        for (var sensor : sensors) {
            SensorData sensorData = new SensorData();
            sensorData.setSensor(sensor);
            sensorData.setCreatedAt(LocalDateTime.now());

            try {
                double temperature = locationService.getTemperatureByCoordinates(sensor.getLatitude(), sensor.getLongitude());
                sensorData.setValue(temperature);
            } catch (Exception e) {
                e.printStackTrace();
                sensorData.setValue(0.0);
            }

            sensorDataRepository.save(sensorData);
        }
    }
}
