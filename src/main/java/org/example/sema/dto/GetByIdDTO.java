package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetByIdDTO {
    @Schema(description = "Id for found element", example = "1")
    @NotBlank(message = "Id is required.")
    private Long id;
}
