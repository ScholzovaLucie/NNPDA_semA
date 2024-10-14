package org.example.sema.service;

import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Device;
import org.example.sema.entities.Sensor;
import org.example.sema.repository.DeviceRepository;
import org.example.sema.repository.SensorRepository;
import org.example.sema.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SensorService {
    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    public Pair<Optional<Sensor>, String> deleteSensorById(Long id) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(id);
        if (optionalSensor.isPresent()) {
            sensorRepository.delete(optionalSensor.get());
            return Pair.of(optionalSensor, "Sensor deleted successfully");
        } else {
            return Pair.of(Optional.empty(), "Sensor not found");
        }
    }

    public Pair<Optional<Sensor>, String> updateSensorById(Long id, String newName) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(id);
        if (optionalSensor.isPresent()) {
            Sensor sensor = optionalSensor.get();
            sensor.setSensorName(newName);
            sensorRepository.save(sensor);
            return Pair.of(Optional.of(sensor), "Sensor updated successfully");
        } else {
            return Pair.of(Optional.empty(), "Sensor not found");
        }
    }

    public List<Sensor> getSensorsByDevice(Long deviceId) {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isPresent()) {
            return sensorRepository.findByDevice(optionalDevice.get());
        }
        return List.of();
    }

    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    public Pair<Optional<ApplicationUser>, String> getUserByUsername(String username) {
        Optional<ApplicationUser> user = userRepository.findByUsername(username);
        return user.map(u -> Pair.of(Optional.of(u), "User found"))
                .orElseGet(() -> Pair.of(Optional.empty(), "User not found"));
    }

    public Pair<Optional<List<Sensor>>, String> getSensorsByUser(ApplicationUser user) {
        List<Device> devices = deviceRepository.findByUsers(user);

        if (devices.isEmpty()) {
            return Pair.of(Optional.empty(), "No devices found for this user");
        }

        // Seznam všech senzorů
        List<Sensor> allSensors = new ArrayList<>();

        // Projdeme každé zařízení a přidáme jeho senzory do seznamu
        for (Device device : devices) {
            List<Sensor> sensors = sensorRepository.findByDevice(device);
            allSensors.addAll(sensors);
        }

        if (allSensors.isEmpty()) {
            return Pair.of(Optional.empty(), "No sensors found for the user's devices");
        }

        return Pair.of(Optional.of(allSensors), "Sensors retrieved successfully");
    }

    public Pair<Optional<Sensor>, String> addSensor(String sensorName) {
        Sensor sensor = new Sensor();
        sensor.setSensorName(sensorName);
        sensorRepository.save(sensor);
        return Pair.of(Optional.of(sensor), "Sensor added successfully");
    }

}
