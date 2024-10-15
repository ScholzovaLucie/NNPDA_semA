package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.example.sema.dtos.*;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Device;
import org.example.sema.entities.Sensor;
import org.example.sema.service.JwtService;
import org.example.sema.service.SensorService;
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
@RequestMapping("/sensors")
@Tag(name = "Sensor", description = "Manage sensors for user devices.")
public class SensorController {
    @Autowired
    private SensorService sensorService;

    @GetMapping("/all")
    @Operation(
            summary = "Get all sensors",
            description = "Retrieve all sensors. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully", content = @Content(schema = @Schema(implementation = Sensor.class)))
            }
    )
    public ResponseEntity<?> getAllSensors() {
        List<Sensor> sensors = sensorService.getAllSensors();
        return ResponseEntity.status(HttpStatus.OK).body(sensors);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get sensors for authenticated user.",
            description = "Retrieve all sensors associated with the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully", content = @Content(schema = @Schema(implementation = Sensor.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<?> getSensorsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser user = (ApplicationUser) authentication.getPrincipal();
        String username = user.getUsername();
        Pair<Optional<ApplicationUser>, String> userResult = sensorService.getUserByUsername(username);

        if (userResult.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userResult.getSecond());
        }

        user = userResult.getFirst().get();
        Pair<Optional<List<Sensor>>, String> result = sensorService.getSensorsByUser(user);

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.getFirst().get());
    }

    @GetMapping("/device/")
    @Operation(
            summary = "Get all sensors for a specific device.",
            description = "Retrieve all sensors for the specified device.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully", content = @Content(schema = @Schema(implementation = Sensor.class))),
                    @ApiResponse(responseCode = "404", description = "Device not found")
            }
    )
    public ResponseEntity<?> getSensorsForDevice(@RequestParam("device_id") Long deviceId) {
        List<Sensor> sensors = sensorService.getSensorsByDevice(deviceId);
        if (sensors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found or no sensors found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sensors);
    }

    @PostMapping("/")
    @Operation(
            summary = "Create a new sensor.",
            description = "Create a new sensor.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sensor added successfully", content = @Content(schema = @Schema(implementation = Sensor.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> addSensor(@RequestBody CreateSensorDTO sensorData) {
        Pair<Optional<Sensor>, String> result = sensorService.addSensor(sensorData);
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getSecond());
    }

    @PostMapping("/assign")
    @Operation(
            summary = "Assign a sensor to a device.",
            description = "Assign an existing sensor to an existing device. Both the sensor and device must already be created. The sensor will be associated with the authenticated user and the specified device.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensor assigned to device successfully", content = @Content(schema = @Schema(implementation = Device.class))),
                    @ApiResponse(responseCode = "404", description = "Sensor or Device not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> addSensorToDevise(@RequestBody AssignSensorDTO data) {
        Pair<Optional<Device>, String> result = sensorService.addSensoreToDevice(data.getSensorId(), data.getDeviceId());

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }

    @PutMapping("/")
    @Operation(
            summary = "Update sensor name.",
            description = "Update the name of a specific sensor by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensor updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Sensor not found")
            }
    )
    public ResponseEntity<?> updateSensorByDeviceAndSensorName(@RequestBody UpdateSensorDTO sensorData) {
        Pair<Optional<Sensor>, String> result = sensorService.updateSensorById(sensorData.getId(), sensorData);

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }

    @DeleteMapping("/")
    @Operation(
            summary = "Delete a sensor by ID.",
            description = "Delete a specific sensor by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensor deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Sensor not found")
            }
    )
    public ResponseEntity<?> deleteSensor(@RequestBody GetByIdDTO data) {
        Pair<Optional<Sensor>, String> result = sensorService.deleteSensorById(data.getId());

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }

    @DeleteMapping("/remove")
    @Operation(
            summary = "Remove a sensor from a device.",
            description = "Remove an existing sensor from an existing device. Both the sensor and the device must already be created. The sensor will be dissociated from the specified device.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensor removed from device successfully"),
                    @ApiResponse(responseCode = "404", description = "Sensor or Device not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> removeSensorFromDevice(@RequestBody AssignSensorDTO data) {
        Pair<Optional<Sensor>, String> result = sensorService.removeSensorFromDevice(data.getSensorId(), data.getDeviceId());

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }
}
