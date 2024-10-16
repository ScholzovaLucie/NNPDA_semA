package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Data
public class CreateSensorDTO {
    @Schema(description = "New name for the sensor", example = "new_sensor_name")
    @NotBlank(message = "Name is required.")
    private String name;

    @Schema(description = "Optional description for the sensor", example = "Temperature sensor located in the main room")
    private String description;
}
