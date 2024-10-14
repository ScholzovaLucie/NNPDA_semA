package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.sema.dtos.UpdateSensorDTO;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.Sensor;
import org.example.sema.service.JwtService;
import org.example.sema.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final JwtService jwtService;

    @GetMapping("/all")
    @Operation(
            summary = "Get all sensors",
            description = "Retrieve all sensors. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully", content = @Content(schema = @Schema(implementation = Sensor.class)))
            }
    )
    public ResponseEntity<?> getAllSensors(@RequestHeader("Authorization") String token) {
        List<Sensor> sensors = sensorService.getAllSensors();
        return ResponseEntity.status(HttpStatus.OK).body(sensors);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get sensors for authenticated user. Requires a valid JWT token.",
            description = "Retrieve all sensors associated with the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully", content = @Content(schema = @Schema(implementation = Sensor.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<?> getSensorsForUser(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.extractUsername(token);
        Pair<Optional<ApplicationUser>, String> userResult = sensorService.getUserByUsername(username);

        if (userResult.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userResult.getSecond());
        }

        ApplicationUser user = userResult.getFirst().get();
        Pair<Optional<List<Sensor>>, String> result = sensorService.getSensorsByUser(user);

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.getFirst().get());
    }

    @GetMapping("/device/")
    @Operation(
            summary = "Get all sensors for a specific device. Requires a valid JWT token.",
            description = "Retrieve all sensors for the specified device. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully", content = @Content(schema = @Schema(implementation = Sensor.class))),
                    @ApiResponse(responseCode = "404", description = "Device not found")
            }
    )
    public ResponseEntity<?> getSensorsForDevice(@RequestBody Long deviceId, @RequestHeader("Authorization") String token) {
        List<Sensor> sensors = sensorService.getSensorsByDevice(deviceId);
        if (sensors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found or no sensors found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sensors);
    }

    @PostMapping("/")
    @Operation(
            summary = "Create a new sensor. Requires a valid JWT token.",
            description = "Create a new sensor. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sensor added successfully", content = @Content(schema = @Schema(implementation = Sensor.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> addSensor(@RequestBody String name, @RequestHeader("Authorization") String token) {
        Pair<Optional<Sensor>, String> result = sensorService.addSensor(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getSecond());
    }

    @DeleteMapping("/")
    @Operation(
            summary = "Delete a sensor by ID. Requires a valid JWT token.",
            description = "Delete a specific sensor by its ID. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensor deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Sensor not found")
            }
    )
    public ResponseEntity<?> deleteSensor(@RequestBody Long id, @RequestHeader("Authorization") String token) {
        Pair<Optional<Sensor>, String> result = sensorService.deleteSensorById(id);

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }

    @PutMapping("/")
    @Operation(
            summary = "Update sensor name. Requires a valid JWT token.",
            description = "Update the name of a specific sensor by its ID. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensor updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Sensor not found")
            }
    )
    public ResponseEntity<?> updateSensorByDeviceAndSensorName(@RequestBody UpdateSensorDTO sensorData, @RequestBody Long id, @RequestHeader("Authorization") String token) {
        Pair<Optional<Sensor>, String> result = sensorService.updateSensorById(id, sensorData.getNewName());

        if (result.getFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getSecond());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.getSecond());
    }
}
