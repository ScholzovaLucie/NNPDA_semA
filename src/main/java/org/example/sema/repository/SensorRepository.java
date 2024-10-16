package org.example.sema.repository;

import org.example.sema.entity.Device;
import org.example.sema.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findBySensorName(String sensorName);

    List<Sensor> findByDevice(Device device);
}
