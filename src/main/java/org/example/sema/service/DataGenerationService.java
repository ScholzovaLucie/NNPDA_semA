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
import java.util.Random;

@Service
@AllArgsConstructor
public class DataGenerationService {

    private SensorDataRepository sensorDataRepository;
    private SensorRepository sensorRepository;
    private DataSender dataSender;
    private final Random random = new Random();

    @Scheduled(fixedRate = 50000)
    public void generateSensorData() {
        List<Sensor> sensors = sensorRepository.findAll();

        for (var sensor : sensors){
            SensorData sensorData = new SensorData();
            sensorData.setSensor(sensor);
            sensorData.setCreated_at(LocalDateTime.now());

            double temperature = 15 + (35 - 15) * random.nextDouble();
            sensorData.setValue(temperature);

            sensorDataRepository.save(sensorData);

            dataSender.sendDataToLogstash(sensorData);
        }

    }
}
