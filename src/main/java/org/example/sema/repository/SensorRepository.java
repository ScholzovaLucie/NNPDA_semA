package org.example.sema.repository;

import org.example.sema.entities.Device;
import org.example.sema.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByDevice(Device device);
}
