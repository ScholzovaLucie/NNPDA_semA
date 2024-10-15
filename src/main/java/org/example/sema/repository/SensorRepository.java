package org.example.sema.repository;

import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Device;
import org.example.sema.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findBySensorName(String sensorName);

    List<Sensor> findByDevice(Device device);
}
