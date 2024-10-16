package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateDeviceDTO {

    @Schema(description = "ID of the device", example = "123")
    @NotBlank(message = "ID is required.")
    private Long id;

    @Schema(description = "New name to be set for the device", example = "new_device_name")
    private String name;

    @Schema(description = "Optional description for the device", example = "Temperature measuring equipment")
    private String description;
}
