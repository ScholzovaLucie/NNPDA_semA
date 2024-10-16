package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateSensorDTO {
    @Schema(description = "ID of the sensor", example = "123")
    @NotBlank(message = "ID is required.")
    private Long id;

    @Schema(description = "New name for the sensor", example = "new_sensor_name")
    private String name;

    @Schema(description = "Optional description for the sensor", example = "Temperature sensor located in the main room")
    private String description;

}
