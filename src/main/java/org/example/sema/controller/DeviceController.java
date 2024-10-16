package org.example.sema.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.sema.dto.AssignDeviceDTO;
import org.example.sema.dto.CreateDeviceDTO;
import org.example.sema.dto.GetByIdDTO;
import org.example.sema.dto.UpdateDeviceDTO;
import org.example.sema.entity.ApplicationUser;
import org.example.sema.entity.Device;
import org.example.sema.response.ServiceResponse;
import org.example.sema.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> createDevice(@Valid @RequestBody CreateDeviceDTO deviceData) {
        try {
            ServiceResponse<Device> result = deviceService.createDevice(deviceData);
            if (result.getData() == null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
    public ResponseEntity<?> addDeviceToUser(@Valid @RequestBody AssignDeviceDTO data) {
        try {
            ServiceResponse<Device> result = deviceService.addDeviceToUser(data.getDeviceId(), data.getUserId());

            if (result.getData() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessage());
            }

            return ResponseEntity.status(HttpStatus.OK).body(result.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
        try {
            ServiceResponse<List<Device>> result = deviceService.getAllDevices();

            if (result.getData().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessage());
            }

            return ResponseEntity.status(HttpStatus.OK).body(result.getData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            ApplicationUser user = (ApplicationUser) authentication.getPrincipal();
            String username = user.getUsername();
            ServiceResponse<List<Device>> result = deviceService.getDevicesForUser(username);

            if (result.getData().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessage());
            }

            return ResponseEntity.status(HttpStatus.OK).body(result.getData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
    public ResponseEntity<?> updateDeviceByName(@Valid @RequestBody UpdateDeviceDTO deviceData) {
        try {
            ServiceResponse<Device> result = deviceService.updateDeviceById(deviceData.getId(), deviceData);

            if (result.getData() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessage());
            }

            return ResponseEntity.status(HttpStatus.OK).body(result.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
    public ResponseEntity<?> deleteDeviceByName(@Valid @RequestBody GetByIdDTO data) {
        try {
            ServiceResponse<Device> result = deviceService.deleteDeviceById(data.getId());

            if (result.getData() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessage());
            }

            return ResponseEntity.status(HttpStatus.OK).body(result.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
    public ResponseEntity<?> removeDeviceFromUser(@Valid @RequestBody AssignDeviceDTO data) {
        try {
            ServiceResponse<Device> result = deviceService.removeDeviceFromUser(data.getDeviceId(), data.getUserId());

            if (result.getData() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessage());
            }

            return ResponseEntity.status(HttpStatus.OK).body(result.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
