package org.example.sema.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.sema.entity.Device;
import org.example.sema.entity.SensorData;
import org.example.sema.response.ServiceResponse;
import org.example.sema.service.SensorDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/sensor/data")
@Tag(name = "Sensors data", description = "Data for sensor.")
public class SensorDataController {
    @Autowired
    private SensorDataService sensorDataService;


    @GetMapping("/all")
    @Operation(
            summary = "Get all data for specific sensor.",
            description = "Create a new device associated with the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Data found", content = @Content(schema = @Schema(implementation = Device.class))),
                    @ApiResponse(responseCode = "404", description = "Data not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
            }
    )
    public ResponseEntity<?> getSensorsData(@Valid @RequestParam("sensor_id") Long sensorId) {
        try {
            ServiceResponse<List<SensorData>> result = sensorDataService.getData(sensorId);
            if (result.getData() != null) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(result.getData());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
