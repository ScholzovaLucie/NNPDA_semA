package org.example.sema.service;

import org.example.sema.dto.CreateDeviceDTO;
import org.example.sema.dto.UpdateDeviceDTO;
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
import java.util.Set;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private UserRepository userRepository;

    public ServiceResponse<Device> createDevice(CreateDeviceDTO deviceData) {
        if (deviceRepository.findByDeviceName(deviceData.getName()).isEmpty()){
            Device device = new Device();
            device.setDeviceName(deviceData.getName());
            device.setDescription(deviceData.getDescription() != null && !deviceData.getDescription().isEmpty() ? deviceData.getDescription() : "");
            device.setSensors(new ArrayList<>());
            deviceRepository.save(device);
            return new ServiceResponse<>(deviceRepository.save(device), "Device created");
        }
        return new ServiceResponse<>(null, "Device already exists!");
    }

    public ServiceResponse<Device> addDeviceToUser(Long deviceId, Long userId) {
        Optional<ApplicationUser> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ServiceResponse<>(null, "User not found");
        }

        ApplicationUser user = optionalUser.get();

        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            return new ServiceResponse<>(null, "Device not found");
        }

        Device device = optionalDevice.get();
        user.getDevices().add(device);
        device.getUsers().add(user);

        userRepository.save(user);
        deviceRepository.save(device);

        return new ServiceResponse<>(device, "Device added successfully");
    }

    public ServiceResponse<List<Device>> getAllDevices() {
        List<Device> devices = deviceRepository.findAll();

        if (devices.isEmpty()) {
            return new ServiceResponse<>(null, "No devices found");
        }

        return new ServiceResponse<>(devices, "Devices retrieved successfully");
    }

    public ServiceResponse<List<Device>> getDevicesForUser(String username) {
        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return new ServiceResponse<>(null, "User not found");
        }

        ApplicationUser user = optionalUser.get();
        List<Device> devices = deviceRepository.findByUsers(user);

        if (devices.isEmpty()) {
            return new ServiceResponse<>(null, "No devices found for user");
        }

        return new ServiceResponse<>(devices, "Devices retrieved successfully");
    }

    public ServiceResponse<Device> updateDeviceById(Long id, UpdateDeviceDTO updateDeviceDTO) {
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
            return new ServiceResponse<>(device, "Device updated successfully");
        } else {
            return new ServiceResponse<>(null, "Device not found");
        }
    }

    public ServiceResponse<Device> deleteDeviceById(Long id) {
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
            return new ServiceResponse<>(optionalDevice.get(), "Device deleted successfully");
        } else {
            return new ServiceResponse<>(null, "Device not found");
        }
    }

    public ServiceResponse<Device> removeDeviceFromUser(Long deviceId, Long userId) {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        Optional<ApplicationUser> optionalUser = userRepository.findById(userId);

        if (optionalDevice.isEmpty()) {
            return new ServiceResponse<>(null, "Device not found");
        }

        if (optionalUser.isEmpty()) {
            return new ServiceResponse<>(null, "User not found");
        }

        Device device = optionalDevice.get();
        ApplicationUser user = optionalUser.get();

        if (user.getDevices().contains(device)) {
            user.getDevices().remove(device);
            userRepository.save(user);
            return new ServiceResponse<>(device, "Device removed from user successfully");
        } else {
            return new ServiceResponse<>(null, "Device is not associated with this user");
        }
    }
}
