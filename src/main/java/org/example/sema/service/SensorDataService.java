package org.example.sema.service;

import org.example.sema.entity.Sensor;
import org.example.sema.entity.SensorData;
import org.example.sema.repository.SensorDataRepository;
import org.example.sema.repository.SensorRepository;
import org.example.sema.response.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private SensorRepository sensorRepository;

    public ServiceResponse<Page<SensorData>> getData(Long sensorId, int page, int size) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorId);
        if (optionalSensor.isPresent()) {
            Sensor sensor = optionalSensor.get();

            Page<SensorData> data = sensorDataRepository.findBySensorId(
                    sensor.getId(),
                    PageRequest.of(page, size, Sort.by("createdAt").descending())
            );

            if (data.isEmpty()) {
                return new ServiceResponse<>(null, "Sensor does not have data");
            }
            return new ServiceResponse<>(data, "Sensor data found");
        } else {
            return new ServiceResponse<>(null, "Sensor does not exist");
        }
    }
}
