package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.sema.dtos.CreateSensorDTO;
import org.example.sema.dtos.UpdateSensorDTO;
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

    @PostMapping("")
    @Operation(
            summary = "Create a sensor for user's device",
            description = "Create a new sensor for the device owned by the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sensor added successfully", content = @Content(schema = @Schema(implementation = Sensor.class))),
                    @ApiResponse(responseCode = "404", description = "User or device not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> addDevice(@RequestBody CreateSensorDTO sensorData, @RequestHeader("Authorization") String token) {

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
            sensor.setSensorName(sensorData.getName());
            sensor.setDevice(device);
            sensorRepository.save(sensor);

            return ResponseEntity.status(HttpStatus.CREATED).body("Sensor added successfully");
        }
        else {
            return ResponseEntity.badRequest().body("Device not found for the given user and device name.");
        }
    }

    @DeleteMapping("")
    @Operation(
            summary = "Delete a sensor by device name and sensor name",
            description = "Delete a specific sensor identified by its name from the device owned by the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensor deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User, device, or sensor not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> deleteSensorByDeviceAndSensorName(@RequestBody CreateSensorDTO sensorData, @RequestHeader("Authorization") String token) {

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
        if (optionalDevice.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found for the given user");
        }

        Device device = optionalDevice.get();

        Optional<Sensor> optionalSensor = sensorRepository.findBySensorNameAndDevice(sensorData.getName(), device);
        if (optionalSensor.isPresent()) {
            sensorRepository.delete(optionalSensor.get());
            return ResponseEntity.ok("Sensor deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sensor not found for the given device and sensor name.");
        }
    }

    @PutMapping("")
    @Operation(
            summary = "Update a sensor by device name and sensor name",
            description = "Update the name of a specific sensor identified by its name for the device owned by the authenticated user. Requires a valid JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sensor updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User, device, or sensor not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> updateSensorByDeviceAndSensorName(@RequestBody UpdateSensorDTO sensorData, @RequestHeader("Authorization") String token) {

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
        if (optionalDevice.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found for the given user");
        }

        Device device = optionalDevice.get();

        Optional<Sensor> optionalSensor = sensorRepository.findBySensorNameAndDevice(sensorData.getName(), device);
        if (optionalSensor.isPresent()) {
            Sensor sensor = optionalSensor.get();
            sensor.setSensorName(sensorData.getNewName());
            sensorRepository.save(sensor);
            return ResponseEntity.ok("Sensor updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sensor not found for the given device and sensor name.");
        }
    }
}
