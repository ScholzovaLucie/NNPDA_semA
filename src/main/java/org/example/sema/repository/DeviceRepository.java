package org.example.sema.repository;

import org.example.sema.entity.ApplicationUser;
import org.example.sema.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUsers(ApplicationUser user);
    Optional<Device> findByDeviceNameAndUsers(String deviceName, ApplicationUser user);

    Optional<Device> findById(Long id);

    Optional<Device> findByDeviceName(String iddeviceName);
}
