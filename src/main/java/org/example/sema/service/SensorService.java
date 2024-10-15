package org.example.sema.service;

import org.example.sema.dtos.CreateSensorDTO;
import org.example.sema.dtos.UpdateSensorDTO;
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

    public Pair<Optional<Sensor>, String> updateSensorById(Long id, UpdateSensorDTO updateSensorDTO) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(id);
        if (optionalSensor.isPresent()) {
            Sensor sensor = optionalSensor.get();

            if (updateSensorDTO.getName() != null && !updateSensorDTO.getName().isEmpty()) {
                sensor.setSensorName(updateSensorDTO.getName());
            }

            if (updateSensorDTO.getDescription() != null && !updateSensorDTO.getDescription().isEmpty()) {
                sensor.setDescription(updateSensorDTO.getDescription());
            }

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

    public Pair<Optional<Sensor>, String> addSensor(CreateSensorDTO sensorData) {
        if (sensorRepository.findBySensorName(sensorData.getName()).isEmpty()){
            Sensor sensor = new Sensor();
            sensor.setSensorName(sensorData.getName());
            sensor.setDescription(sensorData.getDescription() != null && !sensorData.getDescription().isEmpty() ? sensorData.getDescription() : "");
            sensorRepository.save(sensor);
            return Pair.of(Optional.of(sensorRepository.save(sensor)), "Sensor created");
        }
        return Pair.of(Optional.empty(), "Sensor already exists!");
    }

    public Pair<Optional<Device>, String> addSensoreToDevice(Long sensorId, Long deviceId) {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            return Pair.of(Optional.empty(), "Device not found");
        }

        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorId);
        if (optionalSensor.isEmpty()) {
            return Pair.of(Optional.empty(), "Sensor not found");
        }

        Device device = optionalDevice.get();
        Sensor sensor = optionalSensor.get();

        if (sensor.getDevice()!=null){
            return Pair.of(Optional.empty(), "Device already assigned to device.");
        }
        sensor.setDevice(device);
        device.getSensors().add(sensor);

        sensorRepository.save(sensor);
        deviceRepository.save(device);

        return Pair.of(Optional.of(device), "Device added successfully");
    }

    public Pair<Optional<Sensor>, String> removeSensorFromDevice(Long sensorId, Long deviceId) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorId);
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);

        if (optionalSensor.isEmpty()) {
            return Pair.of(Optional.empty(), "Sensor not found");
        }

        if (optionalDevice.isEmpty()) {
            return Pair.of(Optional.empty(), "Device not found");
        }

        Sensor sensor = optionalSensor.get();
        Device device = optionalDevice.get();

        if (device.getSensors().contains(sensor)) {
            device.getSensors().remove(sensor);
            deviceRepository.save(device);
            return Pair.of(Optional.of(sensor), "Sensor removed from device successfully");
        } else {
            return Pair.of(Optional.empty(), "Sensor is not associated with this device");
        }
    }
}
