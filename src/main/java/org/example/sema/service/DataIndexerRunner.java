package org.example.sema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataIndexerRunner implements CommandLineRunner {

    @Autowired
    private DataIndexer dataIndexer;

    @Override
    public void run(String... args) throws Exception {
        dataIndexer.indexAllSensorData();
    }
}
