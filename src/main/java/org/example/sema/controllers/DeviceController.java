package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.sema.dtos.CreateDeviceDTO;
import org.example.sema.dtos.UpdateDeviceDTO;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Device;
import org.example.sema.repository.DeviceRepository;
import org.example.sema.repository.UserRepository;
import org.example.sema.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("")
    @Operation(
            summary = "Create a new device for user",
            description = "Create a new device associated with the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Device created successfully", content = @Content(schema = @Schema(implementation = Device.class))),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> addDevice(@RequestBody CreateDeviceDTO deviceData, @RequestHeader("Authorization") String token) {
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
        device.setDeviceName(deviceData.getName());
        user.getDevices().add(device);
        device.getUsers().add(user);

        userRepository.save(user);
        deviceRepository.save(device);

        return ResponseEntity.status(HttpStatus.CREATED).body("Device added successfully");
    }

    @GetMapping("/all")
    @Operation(
            summary = "Retrieve all devices",
            description = "Retrieve all devices. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Devices retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Device.class))))
            }
    )
    public ResponseEntity<?> getAllDevices(@RequestHeader("Authorization") String token) {
        List<Device> devices = deviceRepository.findAll();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("")
    @Operation(
            summary = "Retrieve devices for user",
            description = "Retrieve all devices associated with the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Devices retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Device.class)))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<?> getMyDevices(@RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.extractUsername(token);

        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        ApplicationUser user = optionalUser.get();

        List<Device> devices = deviceRepository.findByUsers(user);

        return ResponseEntity.ok(devices);
    }

    @PutMapping("")
    @Operation(
            summary = "Update a device by name",
            description = "Update the device name for a specific device associated with the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Device or user not found")
            }
    )
    public ResponseEntity<?> updateDeviceByName(@RequestBody UpdateDeviceDTO deviceData, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.extractUsername(token);

        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        ApplicationUser user = optionalUser.get();

        Optional<Device> optionalDevice = deviceRepository.findByDeviceNameAndUsers(deviceData.getName(), user);
        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();
            device.setDeviceName(deviceData.getNewName());
            deviceRepository.save(device);
            return ResponseEntity.ok("Device updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found");
        }
    }

    @DeleteMapping("")
    @Operation(
            summary = "Delete a device by name",
            description = "Delete a specific device by name associated with the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Device or user not found")
            }
    )
    public ResponseEntity<?> deleteDeviceByName(@RequestBody CreateDeviceDTO deviceData, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.extractUsername(token);


        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        ApplicationUser user = optionalUser.get();

        Optional<Device> optionalDevice = deviceRepository.findByDeviceNameAndUsers(deviceData.getName(), user);
        if (optionalDevice.isPresent()) {
            deviceRepository.delete(optionalDevice.get());
            return ResponseEntity.ok("Device deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found");
        }
    }


}
