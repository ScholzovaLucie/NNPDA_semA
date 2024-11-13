package org.example.sema.service;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.example.sema.entity.SensorData;
import org.example.sema.repository.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DataIndexer {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private RestHighLevelClient client;

    public void indexAllSensorData() {
        List<SensorData> sensorDataList = sensorDataRepository.findAll();

        for (SensorData sensorData : sensorDataList) {
            IndexRequest indexRequest = new IndexRequest("sensor-data")
                    .id(sensorData.getId().toString())
                    .source("value", sensorData.getValue(),
                            "created_at", sensorData.getCreated_at().toString(),
                            "sensor_id", sensorData.getSensor().getId());
            try {
                client.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
