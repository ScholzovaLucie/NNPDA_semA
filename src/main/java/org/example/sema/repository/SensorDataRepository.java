package org.example.sema.repository;

import org.example.sema.entity.ApplicationUser;
import org.example.sema.entity.Device;
import org.example.sema.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findBySensorId(Long id);
}
