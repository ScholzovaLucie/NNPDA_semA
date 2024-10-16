package org.example.sema.service;

import org.example.sema.dto.CreateSensorDTO;
import org.example.sema.dto.UpdateSensorDTO;
import org.example.sema.entity.ApplicationUser;
import org.example.sema.entity.Device;
import org.example.sema.entity.Sensor;
import org.example.sema.repository.DeviceRepository;
import org.example.sema.repository.SensorRepository;
import org.example.sema.repository.UserRepository;
import org.example.sema.response.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ServiceResponse<Sensor> deleteSensorById(Long id) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(id);
        if (optionalSensor.isPresent()) {
            sensorRepository.delete(optionalSensor.get());
            return new ServiceResponse<>(optionalSensor.get(), "Sensor deleted successfully");
        } else {
            return new ServiceResponse<>(null, "Sensor not found");
        }
    }

    public ServiceResponse<Sensor> updateSensorById(Long id, UpdateSensorDTO updateSensorDTO) {
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
            return new ServiceResponse<>(sensor, "Sensor updated successfully");
        } else {
            return new ServiceResponse<>(null, "Sensor not found");
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

    public ServiceResponse<ApplicationUser> getUserByUsername(String username) {
        Optional<ApplicationUser> user = userRepository.findByUsername(username);
        return user.map(u -> new ServiceResponse<>(u, "User found"))
                .orElseGet(() ->new ServiceResponse<>(null, "User not found"));
    }

    public ServiceResponse<List<Sensor>> getSensorsByUser(ApplicationUser user) {
        List<Device> devices = deviceRepository.findByUsers(user);

        if (devices.isEmpty()) {
            return new ServiceResponse<>(null, "No devices found for this user");
        }

        List<Sensor> allSensors = new ArrayList<>();

        for (Device device : devices) {
            List<Sensor> sensors = sensorRepository.findByDevice(device);
            allSensors.addAll(sensors);
        }

        if (allSensors.isEmpty()) {
            return new ServiceResponse<>(null, "No sensors found for the user's devices");
        }

        return new ServiceResponse<>(allSensors, "Sensors retrieved successfully");
    }

    public ServiceResponse<Sensor> addSensor(CreateSensorDTO sensorData) {
        if (sensorRepository.findBySensorName(sensorData.getName()).isEmpty()){
            Sensor sensor = new Sensor();
            sensor.setSensorName(sensorData.getName());
            sensor.setDescription(sensorData.getDescription() != null && !sensorData.getDescription().isEmpty() ? sensorData.getDescription() : "");
            sensorRepository.save(sensor);
            return new ServiceResponse<>(sensorRepository.save(sensor), "Sensor created");
        }
        return new ServiceResponse<>(null, "Sensor already exists!");
    }

    public ServiceResponse<Device> addSensoreToDevice(Long sensorId, Long deviceId) {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            return new ServiceResponse<>(null, "Device not found");
        }

        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorId);
        if (optionalSensor.isEmpty()) {
            return new ServiceResponse<>(null, "Sensor not found");
        }

        Device device = optionalDevice.get();
        Sensor sensor = optionalSensor.get();

        if (sensor.getDevice()!=null){
            return new ServiceResponse<>(null, "Device already assigned to device.");
        }
        sensor.setDevice(device);
        device.getSensors().add(sensor);

        sensorRepository.save(sensor);
        deviceRepository.save(device);

        return new ServiceResponse<>(device, "Device added successfully");
    }

    public ServiceResponse<Sensor> removeSensorFromDevice(Long sensorId, Long deviceId) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorId);
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);

        if (optionalSensor.isEmpty()) {
            return new ServiceResponse<>(null, "Sensor not found");
        }

        if (optionalDevice.isEmpty()) {
            return new ServiceResponse<>(null, "Device not found");
        }

        Sensor sensor = optionalSensor.get();
        Device device = optionalDevice.get();

        if (device.getSensors().contains(sensor)) {
            device.getSensors().remove(sensor);
            deviceRepository.save(device);
            return new ServiceResponse<>(sensor, "Sensor removed from device successfully");
        } else {
            return new ServiceResponse<>(null, "Sensor is not associated with this device");
        }
    }
}
