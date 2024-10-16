package org.example.sema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserDTO {
    @Schema(description = "Id of user", example = "10")
    @NotBlank(message = "Id is required.")
    private Long id;

    @Schema(description = "Username for registration", example = "new_user123")
    private String username;

    @Schema(description = "Email address of the user", example = "user@example.com")
    private String email;
}
