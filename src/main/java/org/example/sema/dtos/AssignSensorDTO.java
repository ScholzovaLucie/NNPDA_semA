package org.example.sema.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssignSensorDTO {
    @Schema(description = "ID of the sensor to be assigned", example = "1")
    @NotBlank(message = "Sensor id is required.")
    private Long sensorId;

    @Schema(description = "ID of the Device to whom the sensor is assigned", example = "10")
    @NotBlank(message = "Device id is required.")
    private Long deviceId;
}
