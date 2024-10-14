package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.sema.dtos.UpdateDeviceDTO;
import org.example.sema.entities.Device;
import org.example.sema.service.DeviceService;
import org.example.sema.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/devices")
@Tag(name = "Device", description = "Manage users devices.")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    private final JwtService jwtService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new device for user. Requires a valid JWT token.",
            description = "Create a new device associated with the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Device created successfully", content = @Content(schema = @Schema(implementation = Device.class))),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> createDevice(@RequestBody String deviceName, @RequestHeader("Authorization") String token) {
        Pair<Optional<Device>, String> device = deviceService.createDevice(deviceName);
        if (device.getFirst().isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(device.getSecond());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(device.getSecond());
    }

    @PostMapping("/assign")
    @Operation(
            summary = "Create a new device for user. Requires a valid JWT token.",
            description = "Create a new device associated with the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Device created successfully", content = @Content(schema = @Schema(implementation = Device.class))),
                    @ApiResponse(responseCode = "404", description = "User or Device not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> addDeviceToUser(@RequestBody Long deviceId, @RequestBody int userId, @RequestHeader("Authorization") String token) {
        Pair<Optional<Device>, String> result = deviceService.addDeviceToUser(deviceId, userId);

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }


    @GetMapping("/all")
    @Operation(
            summary = "Retrieve all devices. Requires a valid JWT token.",
            description = "Retrieve all devices. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Devices retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Device.class))))
            }
    )
    public ResponseEntity<?> getAllDevices(@RequestHeader("Authorization") String token) {
        Pair<Optional<List<Device>>, String> result = deviceService.getAllDevices();

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getFirst().get());
    }

    @GetMapping("/me")
    @Operation(
            summary = "Retrieve devices for user. Requires a valid JWT token.",
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
        Pair<Optional<List<Device>>, String> result = deviceService.getDevicesForUser(username);

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getFirst().get());
    }

    @PutMapping("/")
    @Operation(
            summary = "Update a device by name. Requires a valid JWT token.",
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
        Pair<Optional<Device>, String> result = deviceService.updateDeviceByName(username, deviceData.getName(), deviceData.getNewName());

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }

    @DeleteMapping("/")
    @Operation(
            summary = "Delete a device by name. Requires a valid JWT token.",
            description = "Delete a specific device by name associated with the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Device or user not found")
            }
    )
    public ResponseEntity<?> deleteDeviceByName(@RequestBody Long id, @RequestHeader("Authorization") String token) {
        Pair<Optional<Device>, String> result = deviceService.deleteDeviceById(id);

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }
}
