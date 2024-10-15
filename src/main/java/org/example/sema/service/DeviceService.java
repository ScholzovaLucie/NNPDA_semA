package org.example.sema.service;

import jakarta.validation.constraints.NotBlank;
import org.example.sema.dtos.CreateDeviceDTO;
import org.example.sema.dtos.UpdateDeviceDTO;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Device;
import org.example.sema.entities.Sensor;
import org.example.sema.repository.DeviceRepository;
import org.example.sema.repository.SensorRepository;
import org.example.sema.repository.UserRepository;
import org.springframework.data.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private UserRepository userRepository;

    public Pair<Optional<Device>, String> createDevice(CreateDeviceDTO deviceData) {
        if (deviceRepository.findByDeviceName(deviceData.getName()).isEmpty()){
            Device device = new Device();
            device.setDeviceName(deviceData.getName());
            device.setDescription(deviceData.getDescription() != null && !deviceData.getDescription().isEmpty() ? deviceData.getDescription() : "");
            device.setSensors(new ArrayList<>());
            deviceRepository.save(device);
            return Pair.of(Optional.of(deviceRepository.save(device)), "Device created");
        }
        return Pair.of(Optional.empty(), "Device already exists!");
    }

    public Pair<Optional<Device>, String> addDeviceToUser(Long deviceId, Long userId) {
        Optional<ApplicationUser> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return Pair.of(Optional.empty(), "User not found");
        }

        ApplicationUser user = optionalUser.get();

        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            return Pair.of(Optional.empty(), "Device not found");
        }

        Device device = optionalDevice.get();
        user.getDevices().add(device);
        device.getUsers().add(user);

        userRepository.save(user);
        deviceRepository.save(device);

        return Pair.of(Optional.of(device), "Device added successfully");
    }

    public Pair<Optional<List<Device>>, String> getAllDevices() {
        List<Device> devices = deviceRepository.findAll();

        if (devices.isEmpty()) {
            return Pair.of(Optional.empty(), "No devices found");
        }

        return Pair.of(Optional.of(devices), "Devices retrieved successfully");
    }

    public Pair<Optional<List<Device>>, String> getDevicesForUser(String username) {
        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return Pair.of(Optional.empty(), "User not found");
        }

        ApplicationUser user = optionalUser.get();
        List<Device> devices = deviceRepository.findByUsers(user);

        if (devices.isEmpty()) {
            return Pair.of(Optional.empty(), "No devices found for user");
        }

        return Pair.of(Optional.of(devices), "Devices retrieved successfully");
    }

    public Pair<Optional<Device>, String> updateDeviceById(Long id, UpdateDeviceDTO updateDeviceDTO) {
        Optional<Device> optionalDevice = deviceRepository.findById(id);

        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();

            if (updateDeviceDTO.getName() != null && !updateDeviceDTO.getName().isEmpty()) {
                device.setDeviceName(updateDeviceDTO.getName());
            }

            if (updateDeviceDTO.getDescription() != null && !updateDeviceDTO.getDescription().isEmpty()) {
                device.setDescription(updateDeviceDTO.getDescription());
            }

            deviceRepository.save(device);
            return Pair.of(Optional.of(device), "Device updated successfully");
        } else {
            return Pair.of(Optional.empty(), "Device not found");
        }
    }

    public Pair<Optional<Device>, String> deleteDeviceById(Long id) {
        Optional<Device> optionalDevice = deviceRepository.findById(id);
        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();
            List<Sensor> sensors = sensorRepository.findByDevice(device);
            for (Sensor sensor : sensors) {
                sensor.setDevice(null);
                sensorRepository.save(sensor);
            }

            Set<ApplicationUser> users = device.getUsers();

            for (ApplicationUser user : users){
                user.getDevices().remove(device);
                userRepository.save(user);
            }

            deviceRepository.delete(device);
            return Pair.of(optionalDevice, "Device deleted successfully");
        } else {
            return Pair.of(Optional.empty(), "Device not found");
        }
    }

    public Pair<Optional<Device>, String> removeDeviceFromUser(Long deviceId, Long userId) {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        Optional<ApplicationUser> optionalUser = userRepository.findById(userId);

        if (optionalDevice.isEmpty()) {
            return Pair.of(Optional.empty(), "Device not found");
        }

        if (optionalUser.isEmpty()) {
            return Pair.of(Optional.empty(), "User not found");
        }

        Device device = optionalDevice.get();
        ApplicationUser user = optionalUser.get();

        if (user.getDevices().contains(device)) {
            user.getDevices().remove(device);
            userRepository.save(user);
            return Pair.of(Optional.of(device), "Device removed from user successfully");
        } else {
            return Pair.of(Optional.empty(), "Device is not associated with this user");
        }
    }
}
