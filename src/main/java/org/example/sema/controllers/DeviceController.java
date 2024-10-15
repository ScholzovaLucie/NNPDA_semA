package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.sema.dtos.AssignDeviceDTO;
import org.example.sema.dtos.CreateDeviceDTO;
import org.example.sema.dtos.GetByIdDTO;
import org.example.sema.dtos.UpdateDeviceDTO;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Device;
import org.example.sema.service.DeviceService;
import org.example.sema.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/create")
    @Operation(
            summary = "Create a new device for user.",
            description = "Create a new device associated with the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Device created successfully", content = @Content(schema = @Schema(implementation = Device.class))),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> createDevice(@RequestBody CreateDeviceDTO deviceData) {
        Pair<Optional<Device>, String> device = deviceService.createDevice(deviceData);
        if (device.getFirst().isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(device.getSecond());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(device.getSecond());
    }

    @PostMapping("/assign")
    @Operation(
            summary = "Assign an existing device to a user.",
            description = "Assign an existing device to the specified user. Both the user and the device must already exist. The device will be associated with the user specified by the userId.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device assigned to user successfully", content = @Content(schema = @Schema(implementation = Device.class))),
                    @ApiResponse(responseCode = "404", description = "User or Device not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> addDeviceToUser(@RequestBody AssignDeviceDTO data) {
        Pair<Optional<Device>, String> result = deviceService.addDeviceToUser(data.getDeviceId(), data.getUserId());

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }


    @GetMapping("/all")
    @Operation(
            summary = "Retrieve all devices.",
            description = "Retrieve all devices.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Devices retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Device.class))))
            }
    )
    public ResponseEntity<?> getAllDevices() {
        Pair<Optional<List<Device>>, String> result = deviceService.getAllDevices();

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getFirst().get());
    }

    @GetMapping("/me")
    @Operation(
            summary = "Retrieve devices for user.",
            description = "Retrieve all devices associated with the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Devices retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Device.class)))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<?> getMyDevices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser user = (ApplicationUser) authentication.getPrincipal();
        String username = user.getUsername();
        Pair<Optional<List<Device>>, String> result = deviceService.getDevicesForUser(username);

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getFirst().get());
    }

    @PutMapping("/")
    @Operation(
            summary = "Update a device by name.",
            description = "Update the device name for a specific device associated with the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Device or user not found")
            }
    )
    public ResponseEntity<?> updateDeviceByName(@RequestBody UpdateDeviceDTO deviceData) {
        Pair<Optional<Device>, String> result = deviceService.updateDeviceById(deviceData.getId(), deviceData);

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }

    @DeleteMapping("/")
    @Operation(
            summary = "Delete a device by name.",
            description = "Delete a specific device by name associated with the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Device or user not found")
            }
    )
    public ResponseEntity<?> deleteDeviceByName(@RequestBody GetByIdDTO data) {
        Pair<Optional<Device>, String> result = deviceService.deleteDeviceById(data.getId());

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }

    @DeleteMapping("/remove")
    @Operation(
            summary = "Remove a device from a user.",
            description = "Remove the association between an existing device and a user. Both the device and the user must already exist.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device removed from user successfully"),
                    @ApiResponse(responseCode = "404", description = "User or Device not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> removeDeviceFromUser(@RequestBody AssignDeviceDTO data) {
        Pair<Optional<Device>, String> result = deviceService.removeDeviceFromUser(data.getDeviceId(), data.getUserId());

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }
}
