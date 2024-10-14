package org.example.sema.service;

import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Device;
import org.example.sema.repository.DeviceRepository;
import org.example.sema.repository.UserRepository;
import org.springframework.data.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    public Pair<Optional<Device>, String> createDevice(String deviceName) {
        if (deviceRepository.findByDeviceName(deviceName).isEmpty()){
            Device device = new Device();
            device.setDeviceName(deviceName);

            deviceRepository.save(device);
            return Pair.of(Optional.of(deviceRepository.save(device)), "Device created");
        }
        return Pair.of(Optional.empty(), "Device already exists!");
    }

    public Pair<Optional<Device>, String> addDeviceToUser(Long deviceId, int userId) {
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

    public Pair<Optional<Device>, String> updateDeviceByName(String username, String oldDeviceName, String newDeviceName) {
        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return Pair.of(Optional.empty(), "User not found");
        }

        ApplicationUser user = optionalUser.get();
        Optional<Device> optionalDevice = deviceRepository.findByDeviceNameAndUsers(oldDeviceName, user);

        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();
            device.setDeviceName(newDeviceName);
            deviceRepository.save(device);
            return Pair.of(Optional.of(device), "Device updated successfully");
        } else {
            return Pair.of(Optional.empty(), "Device not found");
        }
    }

    public Pair<Optional<Device>, String> deleteDeviceById(Long id) {
        Optional<Device> optionalDevice = deviceRepository.findById(id);
        if (optionalDevice.isPresent()) {
            deviceRepository.delete(optionalDevice.get());
            return Pair.of(optionalDevice, "Device deleted successfully");
        } else {
            return Pair.of(Optional.empty(), "Device not found");
        }
    }

}
