package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateDeviceDTO {
    @Schema(description = "New name to be set for the device", example = "new_device_name")
    @NotBlank(message = "Name is required.")
    private String name;

    @Schema(description = "Optional description for the device", example = "Temperature measuring equipment", defaultValue = "No description")
    private String description = "";
}
