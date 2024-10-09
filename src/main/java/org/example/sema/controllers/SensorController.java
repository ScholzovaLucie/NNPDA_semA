package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.sema.dtos.CreateSensorDTO;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Device;
import org.example.sema.entities.Sensor;
import org.example.sema.repository.DeviceRepository;
import org.example.sema.repository.SensorRepository;
import org.example.sema.repository.UserRepository;
import org.example.sema.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/sensors")
@Tag(name = "Sensor", description = "Manage sensors for user devices.")
public class SensorController {
    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    private final JwtService jwtService;

    public SensorController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/add")
    @Operation(summary = "Create sensor for users device")
    public ResponseEntity<String> addDevice(@RequestBody CreateSensorDTO sensorData, @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.extractUsername(token);

        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        ApplicationUser user = optionalUser.get();

        Optional<Device> optionalDevice = deviceRepository.findByDeviceNameAndUser(sensorData.getDeviceName(), user);

        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();
            Sensor sensor = new Sensor();
            sensor.setSensorName(sensorData.getSensorName());
            sensor.setDevice(device);
            sensorRepository.save(sensor);

            return ResponseEntity.status(HttpStatus.CREATED).body("Sensor added successfully");
        }
        else {
            return ResponseEntity.badRequest().body("Device not found for the given user and device name.");
        }
    }
}
