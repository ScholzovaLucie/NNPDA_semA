package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.sema.dtos.CreateDeviceDTO;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Device;
import org.example.sema.repository.DeviceRepository;
import org.example.sema.repository.UserRepository;
import org.example.sema.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/devices")
@Tag(name = "Device", description = "Manage users devices.")
public class DeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    private final JwtService jwtService;

    public DeviceController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/add")
    @Operation(summary = "Create new device for user")
    public ResponseEntity<String> addDevice(@RequestBody CreateDeviceDTO deviceData, @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.extractUsername(token);

        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        ApplicationUser user = optionalUser.get();

        Device device = new Device();
        device.setDeviceName(deviceData.getDeviceName());
        device.setUser(user);

        deviceRepository.save(device);

        return ResponseEntity.status(HttpStatus.CREATED).body("Device added successfully");
    }

    @GetMapping("/my-devices")
    @Operation(summary = "Get devices for user")
    public ResponseEntity<List<Device>> getMyDevices(@RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.extractUsername(token);

        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        ApplicationUser user = optionalUser.get();

        List<Device> devices = deviceRepository.findByUser(user);

        return ResponseEntity.ok(devices);
    }
}
