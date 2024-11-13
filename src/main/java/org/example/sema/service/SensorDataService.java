package org.example.sema.service;

import org.elasticsearch.client.RequestOptions;
import org.example.sema.entity.Sensor;
import org.example.sema.entity.SensorData;
import org.example.sema.repository.SensorDataRepository;
import org.example.sema.repository.SensorRepository;
import org.example.sema.response.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.io.IOException;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private SensorRepository sensorRepository;

    public ServiceResponse<List<SensorData>> getData(Long sensorId) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorId);
        if (optionalSensor.isPresent()) {
            Sensor sensor = optionalSensor.get();
            List<SensorData> data = sensorDataRepository.findBySensorId(sensor.getId());
            if (data.isEmpty()){
                return new ServiceResponse<>(null, "Sensor does not have data");
            }
            return new ServiceResponse<>(data, "Sensor data found");
        }else {
            return new ServiceResponse<>(null, "Sensor not exist");
        }
    }

}
