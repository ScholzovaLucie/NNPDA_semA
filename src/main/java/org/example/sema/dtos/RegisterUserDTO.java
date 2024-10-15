package org.example.sema.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterUserDTO {

    @Schema(description = "Username for registration", example = "new_user123")
    @NotBlank(message = "Username is required.")
    private String username;

    @Schema(description = "Password for registration", example = "password123")
    @NotBlank(message = "Password is required.")
    private String password;

    @Schema(description = "Email address of the user", example = "user@example.com")
    @NotBlank(message = "Email is required.")
    private String email;
}
