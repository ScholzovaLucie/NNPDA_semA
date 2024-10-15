package org.example.sema.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssignDeviceDTO {
    @Schema(description = "ID of the device to be assigned", example = "1")
    @NotBlank(message = "Device id is required.")
    private Long deviceId;

    @Schema(description = "ID of the user to whom the device is assigned", example = "10")
    @NotBlank(message = "User id is required.")
    private Long userId;
}
